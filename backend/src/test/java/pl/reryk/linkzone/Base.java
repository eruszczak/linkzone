package pl.reryk.linkzone;

import pl.reryk.linkzone.dto.AccountCreate;
import pl.reryk.linkzone.dto.CommentCreate;
import pl.reryk.linkzone.dto.PostCreate;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.model.Comment;
import pl.reryk.linkzone.model.Group;
import pl.reryk.linkzone.model.Post;
import pl.reryk.linkzone.security.JwtTokenProvider;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import pl.reryk.linkzone.service.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Random;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

//import com.example.reddit.service.model.*;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
//@WebAppConfiguration
public class Base {

    protected static boolean setUpIsDone = false;
    protected final String userName = "admin11";
    protected final String groupName = "nameGroup";
    protected final String fieldEmail = "email";
    protected final String fieldUsername = "username";
    protected final String fieldPassword = "password";
    protected final String fieldPasswordConfirm = "passwordConfirm";
    protected final String fieldUsernameOrEmail = "usernameOrEmail";
    protected final String fieldGroupName = "name";
    protected final String fieldGroupDescription = "description";
    protected final String fieldPostTitle = "title";
    protected final String fieldPostContent = "content";
    protected final String fieldCommentContent = "content";
    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected Account account;
    protected String password = "123456";
    protected String email = "test@gmail.com";
    protected MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    protected MockMvc mockMvc;
    protected String token;
    @Autowired
    protected AccountService accountService;
    @Autowired
    protected PostService postService;
    @Autowired
    protected GroupService groupService;
    @Autowired
    protected CommentService commentService;
    @Autowired
    protected GroupMembershipService groupMembershipService;
    @Autowired
    protected JwtTokenProvider jwtTokenProvider;
    protected Group group;
    protected Post post;
    protected Comment comment;
    @Autowired
    PasswordEncoder passwordEncoder;

    //    @Before
    public void runOnlyOnce() {
        if (setUpIsDone) {
            return;
        }
        setUpIsDone = true;
    }

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        account = accountService.create(getAccountCreate(userName, password, email));
        token = getToken(userName, password);

        Group testGroup = new Group();
        testGroup.setName(groupName);
        testGroup.setCreator(account);
//        testGroup.addAdministrator(user3);
        group = groupService.save(testGroup);
        System.out.println("SETUP");

        PostCreate postCreate = new PostCreate();
        postCreate.setTitle("weqeqw");
        postCreate.setContent("dasqweq");
//        post = postService.create(postCreate, group, account);

        CommentCreate commentCreate = new CommentCreate();
        commentCreate.setContent("dseqwewq");
        comment = commentService.create(commentCreate, post, account);

//        runOnlyOnce();
    }

    @After
    public void cleanup() {
//        commentService.deleteAll(); // comments should be removed when posts are removed
        postService.deleteAll();
//        groupMembershipService.deleteAll();  // should be removed when groups or accounts are removed
        groupService.deleteAll();
        accountService.deleteAll();
    }

    protected HttpHeaders getAuthHeader(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        return httpHeaders;
    }

    protected MvcResult login(JSONObject json) throws Exception {
        return mockMvc.perform(post("/users" + "/login")
                .contentType(contentType)
                .content(json.toString()))
                .andExpect(status().isOk())
                .andReturn();
    }

    protected String getToken(String usernameOrEmail, String password) throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldUsernameOrEmail, usernameOrEmail);
        json.put(fieldPassword, password);
        return extractToken(login(json));
    }

    protected String getToken(Account account) {
        return jwtTokenProvider.generateToken(account);
    }

    protected String extractToken(MvcResult result) throws UnsupportedEncodingException {
        return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    protected AccountCreate getAccountCreate(String username, String password) {
        return getAccountCreate(username, password, username + "@gmail.com");
    }

    protected AccountCreate getAccountCreate(String username, String password, String email) {
        AccountCreate accountCreate = new AccountCreate();
        accountCreate.setUsername(username);
        accountCreate.setPassword(password);
        accountCreate.setPasswordConfirm(password);
        accountCreate.setEmail(email);
        return accountCreate;
    }

    protected Account createAccount() {
        Random ran = new Random();
        int x = ran.nextInt(1000000);
        return accountService.create(getAccountCreate("admingroup" + x, password));
    }
}
