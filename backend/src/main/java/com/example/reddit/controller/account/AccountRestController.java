package com.example.reddit.controller.account;

import com.example.reddit.dto.*;
import com.example.reddit.exception.EmailTakenException;
import com.example.reddit.exception.UserNotFoundException;
import com.example.reddit.exception.UsernameTakenException;
import com.example.reddit.exception.ValidationErrorException;
import com.example.reddit.model.Account;
import com.example.reddit.resource.AccountResource;
import com.example.reddit.resource.AccountResourceAssembler;
import com.example.reddit.security.CurrentUser;
import com.example.reddit.security.JwtTokenProvider;
import com.example.reddit.security.UserPrincipal;
import com.example.reddit.service.AccountService;
import com.example.reddit.service.FileStorageService;
import com.example.reddit.utils.ModelMapper;
import com.example.reddit.utils.MultipartFileValidator;
import com.example.reddit.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/api/users")
public class AccountRestController {

    private final AccountService accountService;
    private final AccountResourceAssembler accountResourceAssembler;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final FileStorageService fileStorageService;

    @Autowired
    public AccountRestController(AccountService accountService,
                                 AccountResourceAssembler accountResourceAssembler,
                                 JwtTokenProvider tokenProvider,
                                 AuthenticationManager authenticationManager, FileStorageService fileStorageService) {
        this.accountService = accountService;
        this.accountResourceAssembler = accountResourceAssembler;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/login/")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return new ResponseEntity<>(new JwtAuthenticationResponse(jwt), HttpStatus.OK);
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(Pageable pageable, PagedResourcesAssembler<Account> pagedAssembler) {
        PagedResources<AccountResource> pagedResources = pagedAssembler.toResource(
                accountService.findAll(pageable), accountResourceAssembler);
        return new ResponseEntity<>(pagedResources, HttpStatus.OK);
    }

    @GetMapping(value = "/findExact")
    public ResponseEntity<?> findByExactUsername(@RequestParam("username") @NotNull String username) {
        try {
            Account account = accountService.findByUsername(username);
            return new ResponseEntity<>(new AccountSummary(account), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> create(
            @Valid @RequestBody AccountCreate accountCreate,
            Errors errors) {
        validateUsername(accountCreate.getUsername());
        validateEmail(accountCreate.getEmail());
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        Account account = accountService.create(accountCreate);
        AccountSummary accountSummary = ModelMapper.mapAccountToSummary(account);
        return new ResponseEntity<>(new AccountResource(accountSummary), HttpStatus.CREATED);
    }

    @GetMapping(value = "/details")
    public AccountDetails getCurrentUserInfo(@CurrentUser UserPrincipal currentUser) {
        return new AccountDetails(currentUser.getAccount());
    }

    @GetMapping(value = "/groupInfo/{username}")
    public ResponseEntity<?> getExtras(@PathVariable String username) {
        Account account = accountService.findByUsernameEager(username);
        return new ResponseEntity<>(new AccountGroupInfo(account), HttpStatus.OK);
    }

    @GetMapping(value = "/{username}")
    public AccountDetails detail(@PathVariable String username) {
        // TODO do not expose an email
        return new AccountDetails(accountService.findByUsername(username));
    }

    @GetMapping(value = "/{username}/stats")
    public IAccountStatsDto stats(@PathVariable String username) {
        return accountService.calculateStats(accountService.findByUsername(username).getId());
    }

    @PutMapping(value = "/{username}")
    public ResponseEntity<?> update(@PathVariable String username,
                                    @Valid @RequestBody AccountUpdate accountUpdate,
                                    Errors errors,
                                    @CurrentUser UserPrincipal currentUser) {
        Utils.checkIfOwner(currentUser, username);
        Account account = accountService.findByUsername(username);
        // validate username only if it has changed
        if (!account.getUsername().equals(accountUpdate.getUsername())) {
            validateUsername(accountUpdate.getUsername());
        }
        if (!account.getEmail().equals(accountUpdate.getEmail())) {
            validateEmail(accountUpdate.getEmail());
        }
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        accountService.update(account, accountUpdate);
        return new ResponseEntity<>(new AccountDetails(account), HttpStatus.OK);
    }

    @PostMapping(value = "/{username}/upload-avatar")
    public AccountDetails uploadAvatar(@RequestParam("data") MultipartFile file,
                                           @PathVariable("username") String username,
                                           @CurrentUser UserPrincipal currentUser) {
        Utils.checkIfOwner(currentUser, username);
        MultipartFileValidator.validate(file);
        MultipartFileValidator.validateImageDimensions(file, 200, 200);
        MultipartFileValidator.validateImageSize(file, 100);
        String fileName = fileStorageService.storeFile(file);
        accountService.updateAvatar(fileName, currentUser.getId());
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(fileName)
                .toUriString();

        fileStorageService.removeFile(currentUser.getAccount().getAvatar());

        return detail(currentUser.getUsername());
    }

    @DeleteMapping(value = "/{username}")
    public ResponseEntity<Void> delete(@PathVariable String username,
                                       @CurrentUser UserPrincipal currentUser) {
        Utils.checkIfOwner(currentUser, username);
        Account account = accountService.findByUsername(username);
        accountService.delete(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/checkUsername/{username}/")
    public ResponseEntity<?> checkUsernameAvailability(@PathVariable("username") String username) {
        boolean isAvailable = true;
        try {
            validateUsername(username);
        } catch (UsernameTakenException e) {
            isAvailable = false;
        }
        return new ResponseEntity<>(new IsAvailableResponse(isAvailable), HttpStatus.OK);
    }

    @GetMapping(value = "/checkEmail/{email}/")
    public ResponseEntity<?> checkEmailAvailability(@PathVariable("email") String email) {
        boolean isAvailable = true;
        try {
            validateEmail(email);
        } catch (EmailTakenException e) {
            isAvailable = false;
        }
        return new ResponseEntity<>(new IsAvailableResponse(isAvailable), HttpStatus.OK);
    }

    private void validateUsername(String updatedUsername) {
        try {
            accountService.findByUsername(updatedUsername);
            throw new UsernameTakenException(updatedUsername);
        } catch (UserNotFoundException e) {
        }
    }

    private void validateEmail(String email) {
        try {
            accountService.findByEmail(email);
            throw new EmailTakenException(email);
        } catch (UserNotFoundException e) {
        }
    }
}
