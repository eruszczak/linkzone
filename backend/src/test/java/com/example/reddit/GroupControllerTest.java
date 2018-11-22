package com.example.reddit;

import com.example.reddit.controller.account.AccountRestController;
import com.example.reddit.controller.group.GroupRestController;
import com.example.reddit.dto.AccountCreate;
import com.example.reddit.dto.GroupCreate;
import com.example.reddit.exception.AlreadyExistsException;
import com.example.reddit.exception.NotFoundException;
import com.example.reddit.exception.UsernameTakenException;
import com.example.reddit.model.Group;
import com.example.reddit.validation.annotation.NoSpacesConstraint;
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
public class GroupControllerTest extends Base {

    @Test
    public void tokenIsRequiredToCreateGroup() throws Exception {
        JSONObject json = new JSONObject();
        mockMvc.perform(post("/groups" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createGroup() throws Exception {
        JSONObject json = new JSONObject();
        String groupName = "name";
        json.put(fieldGroupName, groupName);
        json.put(fieldGroupDescription, "description");
        boolean thrown = false;
        try {
            groupService.findByName(groupName);
        } catch (NotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
        mockMvc.perform(post("/groups" + "/")
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isCreated());
        thrown = false;
        Group group = null;
        try {
            group = groupService.findByName(groupName);
        } catch (NotFoundException e) {
            thrown = true;
        }
        Assert.assertFalse(thrown);
        Assert.assertNotNull(group);
        Assert.assertEquals(group.getCreator().getId(), account.getId());
    }

    @Test
    public void createGroupNotUniqueName() throws Exception {
        JSONObject json = new JSONObject();
        String groupName = "naqweqwweme";
        json.put(fieldGroupName, groupName);
        json.put(fieldGroupDescription, "description");
        mockMvc.perform(post("/groups" + "/")
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/groups" + "/")
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(new AlreadyExistsException(Group.class, groupName).getMessage())));
        json.put(fieldGroupName, groupName.toUpperCase());
        mockMvc.perform(post("/groups" + "/")
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(new AlreadyExistsException(Group.class, groupName.toUpperCase()).getMessage())));
    }

    @Test
    public void groupNameMustContainNoSpaces() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldGroupName, "e dasdsa");
        mockMvc.perform(post("/groups" + "/")
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(NoSpacesConstraint.MESSAGE)));
    }

    @Test
    public void groupIsRetrieved() throws Exception {
        mockMvc.perform(get("/groups" + "/" + group.getName())
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void groupIsNotRetrieved() throws Exception {
        mockMvc.perform(get("/groups" + "/" + "notexistinggroupname")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateGroup() throws Exception {
        String newName = "newGroupName";
        String newDescription = "newDescription";
        JSONObject json = new JSONObject();
        json.put(fieldGroupName, newName);
        json.put(fieldGroupDescription, newDescription);
        mockMvc.perform(put("/groups" + "/" + group.getName())
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is(newName)))
                .andExpect(jsonPath("$.description", is(newDescription)));
//        Group group = groupService.findById(group.getId());
    }

    @Test
    public void subscribeGroup() throws Exception {
        boolean thrown = false;
        try {
            groupMembershipService
                    .findByAccountUsernameAndGroupName(account.getUsername(), group.getName());
        } catch (NotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
        String url = "/groups" + "/" + group.getName() + "/membership/";
        mockMvc.perform(post(url)
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isCreated());
        thrown = false;
        try {
            groupMembershipService
                    .findByAccountUsernameAndGroupName(account.getUsername(), group.getName());
        } catch (NotFoundException e) {
            thrown = true;
        }
        Assert.assertFalse(thrown);
        // if already subscribed, do not modify
        mockMvc.perform(post(url)
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void unsubscribeGroup() throws Exception {
        String url = "/groups" + "/" + group.getName() + "/membership/";
        // subscribe
        mockMvc.perform(post(url)
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isCreated());
        // unsubscribe
        mockMvc.perform(delete(url)
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andExpect(status().isOk());
        // try unsub again
        mockMvc.perform(delete(url)
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteGroup() throws Exception {
        // TODO check if posts are removed
        mockMvc.perform(delete("/groups" + "/" + group.getName())
            .contentType(contentType)
            .headers(getAuthHeader(token)))
            .andExpect(status().isOk());
        // try to delete once again
        mockMvc.perform(delete("/groups" + "/" + group.getName())
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(new NotFoundException(Group.class, group.getName()).getMessage())));
    }

    @Test
    public void canUpdateGroupWithoutChangingItsName() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldGroupName, group.getName());
        String description = "newDescriptiond asd asdsaasdweqqwe";
        json.put(fieldGroupDescription, description);
        mockMvc.perform(put("/groups" + "/" + group.getName())
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(description)));
    }

    @Test
    public void updatedGroupNameMustBeUnique() throws Exception {
        JSONObject json = new JSONObject();
        GroupCreate groupCreate = new GroupCreate();
        String name = "someName1231";
        groupCreate.setName(name);
        groupService.create(groupCreate, account);
        // try to update a group with a name that is already taken
        json.put(fieldGroupName, name);
        mockMvc.perform(put("/groups" + "/" + group.getName())
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(new AlreadyExistsException(Group.class, name).getMessage())));
    }

    @Test
    public void updatedGroupNameMustContainNoSpaces() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldGroupName, "naqw eqwweme");
        mockMvc.perform(put("/groups" + "/" + group.getName())
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(NoSpacesConstraint.MESSAGE)));
    }

    // subs to 2 groups and returned 2 records
}
