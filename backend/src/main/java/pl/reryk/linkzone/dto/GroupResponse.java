package pl.reryk.linkzone.dto;

import pl.reryk.linkzone.model.PostType;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.model.Group;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GroupResponse {
    public long id;
    public String name;
    public String description;
    public Instant createdAt;
    public String bannerUrl;
    public String logo;
    public boolean isDefault;

    public boolean isSubbed;
    public int subscribers;
    public int postCount;

    public List<AccountSummary> administrators;
    public List<AccountSummary> moderators;
    public List<PostType> postTypes;
    public List<String> tags;

    public AccountSummary creator;
    public boolean isCreator;
    public boolean isModerator;
    public boolean isAdministrator;
    public String groupStatus;

    public GroupResponse(Group group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.subscribers = 14654;
        this.id = group.getId();
        this.creator = new AccountSummary(group.getCreator());
        this.administrators = group.getAdministrators().stream().map(AccountSummary::new).collect(Collectors.toList());
        this.moderators = group.getModerators().stream().map(AccountSummary::new).collect(Collectors.toList());
        this.postTypes = group.getPostTypes();
        this.tags = group.getTags();
        this.bannerUrl = group.getBannerUrl();
        this.logo = group.getLogo();
        this.isDefault = group.isDefault();
    }

    public GroupResponse(IGroupResponseDto dto) {
        id = dto.getId();
        name = dto.getName();
        description = dto.getDescription();
        createdAt = dto.getCreatedAt();
        bannerUrl = dto.getBannerUrl();
        isSubbed = dto.getIsSubbed() > 0;
        subscribers = dto.getSubscribers();
        logo = dto.getLogo();
        isDefault = dto.getIsDefault();
        postCount = dto.getPostCount() != null ? dto.getPostCount() : 0;
        if (dto.getPostTypes() != null) {
            postTypes = Arrays.stream(dto.getPostTypes().split(",")).map(PostType::valueOf).collect(Collectors.toList());
        }
        this.groupStatus = dto.getGroupStatus();
    }

    public GroupResponse(IGroupResponseDto dto, Group group, Account requestUser) {
        this(dto);
        this.creator = new AccountSummary(group.getCreator());
        this.administrators = group.getAdministrators().stream().map(AccountSummary::new).collect(Collectors.toList());
        this.moderators = group.getModerators().stream().map(AccountSummary::new).collect(Collectors.toList());
        this.postTypes = group.getPostTypes();
        this.tags = group.getTags();
        if (requestUser != null) {
            isCreator = requestUser.isAdmin() || group.getCreator().equals(requestUser);
            isAdministrator = isCreator || group.getAdministrators().contains(requestUser);
            isModerator = isAdministrator || group.getModerators().contains(requestUser);
        }
    }
}