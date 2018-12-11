package com.example.reddit;

import com.example.reddit.dto.PostCreate;
import com.example.reddit.dto.PostUpdate;
import com.example.reddit.exception.NotFoundException;
import com.example.reddit.model.Group;
import com.example.reddit.utils.Utils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedditApplication.class)
@WebAppConfiguration
public class PostControllerTest extends Base {

    @Test
    public void createPost() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldPostTitle, "title");
        json.put(fieldPostContent, "   content   ");
        mockMvc.perform(post("/groups" + "/" + group.getName() + "/posts" + "/")
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is("content".trim())));
        // get ID and check if it has group and account TODO
    }

    @Test
    public void tokenIsRequiredToCreatePost() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldPostTitle, "title");
        json.put(fieldPostContent, "content");
        mockMvc.perform(post("/groups" + "/" + group.getName() + "/posts" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        // get ID and check if it has group and account TODO
    }

    @Test
    public void groupMustExist() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldPostTitle, "title");
        json.put(fieldPostContent, "content");
        mockMvc.perform(post("/groups" + "/" + "randomGroupName321" + "/posts" + "/")
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(new NotFoundException(Group.class, "randomGroupName321").getMessage())));
    }

    @Test
    public void updatePost() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(newTitle.trim())))
                .andExpect(jsonPath("$.content", is(newContent.trim())));
    }

    @Test
    public void deletePost() throws Exception {
        //  TODO removes its replies
        mockMvc.perform(delete("/posts" + "/" + post.getId())
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isOk());
        boolean thrown = false;
        try {
            postService.findById(post.getId());
        } catch (NotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
        mockMvc.perform(delete("/posts" + "/" + post.getId())
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void postIsRetrieved() throws Exception {
        mockMvc.perform(get("/posts" + "/" + post.getId())
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void postIsNotRetrieved() throws Exception {
        mockMvc.perform(get("/posts" + "/" + 412311)
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void postSlugIsCreatedAndUpdatedCorrectly() throws Exception {
        PostCreate postCreate = new PostCreate();
        String postTitle = "wewqdsadsa";
        postCreate.setTitle(postTitle);
        postCreate.setContent("dasqweq");
//        Post post = postService.create(postCreate, group, account);
        Assert.assertEquals(post.getSlug(), Utils.getSlug(postTitle));
        // see if slug is updated when title is changed
        String newPostTitle = "hey this if what i found :)";
        PostUpdate postUpdate = new PostUpdate();
        postUpdate.setTitle(newPostTitle);
        postUpdate.setContent("dasqweq");
        post = postService.updateNoPerms(post, postUpdate);
        Assert.assertEquals(post.getSlug(), Utils.getSlug(newPostTitle));
    }
}
