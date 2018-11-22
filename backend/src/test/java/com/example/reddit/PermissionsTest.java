package com.example.reddit;

import com.example.reddit.controller.account.AccountRestController;
import com.example.reddit.controller.comment.CommentRestController;
import com.example.reddit.controller.group.GroupRestController;
import com.example.reddit.controller.post.PostRestController;
import com.example.reddit.dto.CommentCreate;
import com.example.reddit.dto.PostCreate;
import com.example.reddit.model.Account;
import com.example.reddit.model.Comment;
import com.example.reddit.model.Group;
import com.example.reddit.model.Post;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedditApplication.class)
@WebAppConfiguration
public class PermissionsTest extends Base {

    // GROUPS
    @Test
    public void randomCannotUpdateOrDeleteGroup() throws Exception {
        String randomAccountToken = getToken(createAccount());
        updateGroup(randomAccountToken, status().isForbidden());
        deleteGroup(randomAccountToken, status().isForbidden());
    }

    @Test
    public void creatorCanUpdateGroup() throws Exception {
        updateGroup(token, status().isOk());
    }

    @Test
    public void creatorCanDeleteGroup() throws Exception {
        deleteGroup(token, status().isOk());
    }

    @Test
    public void adminCanUpdateGroup() throws Exception {
        String adminToken = createGroupAdminAndGetHisToken(group);
        updateGroup(adminToken, status().isOk());
    }

    @Test
    public void adminCanDeleteGroup() throws Exception {
        String adminToken = createGroupAdminAndGetHisToken(group);
        deleteGroup(adminToken, status().isOk());
    }

    private String createGroupAdminAndGetHisToken(Group group) {
        Account admin = createAccount();
        group.addAdministrator(admin);
        groupService.save(group);
        return getToken(admin);
    }

    private String createGroupModeratorAndGetHisToken(Group group) {
        Account moderator = createAccount();
        group.addModerator(moderator);
        groupService.save(group);
        return getToken(moderator);
    }

    private void deleteGroup(String token, ResultMatcher status) throws Exception {
        mockMvc.perform(delete("/groups" + "/" + group.getName())
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status);
    }

    private void updateGroup(String token, ResultMatcher status) throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldGroupName, "newGroupName");
        json.put(fieldGroupDescription, "newDescription");
        mockMvc.perform(put("/groups" + "/" + group.getName())
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status);
    }

    // admin can add/remove admins/mods
    // owner can do everything
    // what about cascading

    // POSTS
    @Test
    public void randomCannotUpdateOrDeletePost() throws Exception {
        String randomAccountToken = getToken(createAccount());
        updatePost(randomAccountToken, status().isForbidden());
        deletePost(randomAccountToken, status().isForbidden());
    }

    private void deletePost(String token, ResultMatcher status) throws Exception {
        mockMvc.perform(delete("/posts" + "/" + post.getId())
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    public void creatorCanUpdateOrDeletePost() throws Exception {
        updatePost(token, status().isOk());
        deletePost(token, status().isOk());
    }

    @Test
    public void adminGroupCanUpdateOrDeletePost() throws Exception {
        String token = createGroupAdminAndGetHisToken(group);
        updatePost(token, status().isOk());
        deletePost(token, status().isOk());
    }

    @Test
    public void moderatorGroupCanUpdateOrDeletePost() throws Exception {
        String token = createGroupModeratorAndGetHisToken(group);
        updatePost(token, status().isOk());
        deletePost(token, status().isOk());
    }

    private void updatePost(String token, ResultMatcher status) throws Exception {
        JSONObject json = new JSONObject();
        String newTitle = " new title ";
        String newContent = " eqweqwewq weq";
        json.put(fieldPostTitle, newTitle);
        json.put(fieldPostContent, newContent);
        mockMvc.perform(put("/posts" + "/" + post.getId())
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status);
    }

    // COMMENT

    @Test
    public void randomCannotRemoveOrUpdateComment() throws Exception {
        String token = getToken(createAccount());
        updateComment(token, status().isForbidden(), comment.getId());
        removeComment(token, status().isForbidden(), comment.getId());
    }

    @Test
    public void ownerCanRemoveOrUpdateComment() throws Exception {
        updateComment(token, status().isOk(), comment.getId());
        removeComment(token, status().isOk(), comment.getId());
    }

    @Test
    public void postOwnerCanRemoveCommentsButCannotUpdateIt() throws Exception {
        Account postOwner = createAccount();
        Account commentOwner = createAccount();
        PostCreate postCreate = new PostCreate();
        postCreate.setTitle("weqeqw");
        postCreate.setContent("dasqweq");
        Post post = postService.create(postCreate, group, postOwner);
        CommentCreate commentCreate = new CommentCreate();
        commentCreate.setContent("dseqwewq");
        Comment comment = commentService.create(commentCreate, post, commentOwner);
        updateComment(getToken(postOwner), status().isForbidden(), comment.getId());
        removeComment(getToken(postOwner), status().isOk(), comment.getId());
    }

    @Test
    public void commentOwnerCanRemoveItsRepliesButNotUpdate() throws Exception {
        Account replyOwner = createAccount();
        Account parentOwner = createAccount();
        CommentCreate commentCreate = new CommentCreate();
        commentCreate.setContent("dseqwewq");
        Comment parent = commentService.create(commentCreate, post, parentOwner);
        commentCreate = new CommentCreate();
        commentCreate.setContent("dseqwewq");
        Comment reply1 = commentService.createReply(parent, commentCreate, replyOwner);
        updateComment(getToken(parentOwner), status().isForbidden(), reply1.getId());
        removeComment(getToken(parentOwner), status().isOk(), reply1.getId());
    }

    @Test
    public void groupOwnerCanRemoveOrUpdateComment() throws Exception {
        Account groupOwner = createAccount();
        Account postOwner = createAccount();
        Account commentOwner = createAccount();
        // create group
        Group testGroup = new Group();
        testGroup.setName("smsdadsa123");
        testGroup.setCreator(groupOwner);
        Group group = groupService.save(testGroup);
        // create post in a group
        PostCreate postCreate = new PostCreate();
        postCreate.setTitle("weqeqw");
        Post post = postService.create(postCreate, group, postOwner);
        // create comment in a post
        CommentCreate commentCreate = new CommentCreate();
        commentCreate.setContent("dseqwewq");
        Comment comment = commentService.create(commentCreate, post, commentOwner);
        // check if groupOwner can update/remove
        updateComment(getToken(groupOwner), status().isOk(), comment.getId());
        removeComment(getToken(groupOwner), status().isOk(), comment.getId());
        // check if groupAdmin can update/remove
        String groupAdminToken = createGroupAdminAndGetHisToken(group);
        comment = commentService.create(commentCreate, post, commentOwner);
        updateComment(groupAdminToken, status().isOk(), comment.getId());
        removeComment(groupAdminToken, status().isOk(), comment.getId());
        // check if groupMod can update/remove
        String groupModToken = createGroupModeratorAndGetHisToken(group);
        comment = commentService.create(commentCreate, post, commentOwner);
        updateComment(groupModToken, status().isOk(), comment.getId());
        removeComment(groupModToken, status().isOk(), comment.getId());
    }

    private void updateComment(String token, ResultMatcher status, long commentId) throws Exception {
        JSONObject json = new JSONObject();
        String newContent = "dasdae qwewqewq   213  ";
        json.put(fieldCommentContent, newContent);
        mockMvc.perform(put("/comments" + "/" + commentId)
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status);
    }

    private void removeComment(String token, ResultMatcher status, long commentId) throws Exception {
        mockMvc.perform(delete("/comments" + "/" + commentId)
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status);
    }
}
