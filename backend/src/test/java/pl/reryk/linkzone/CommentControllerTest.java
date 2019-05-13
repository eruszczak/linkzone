package pl.reryk.linkzone;

import org.junit.Ignore;
import pl.reryk.linkzone.dto.CommentCreate;
import pl.reryk.linkzone.exception.NotFoundException;
import pl.reryk.linkzone.model.Comment;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Ignore
public class CommentControllerTest extends Base {

    @Test
    public void createComment() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldCommentContent, "  some content ");
        mockMvc.perform(post("/posts" + "/" + post.getId() + "/comments" + "/")
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is("  some content ".trim())));
        // get id and check if account/post is assigned
    }

    @Test
    public void commentIsRetrieved() throws Exception {
        mockMvc.perform(get("/comments" + "/" + comment.getId())
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void createReplyToComment() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldCommentContent, "  some reply ");
        mockMvc.perform(post("/comments" + "/" + comment.getId())
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is("  some reply ".trim())));
//        System.out.println(accountService.find);
        // get id and check if account/post is assigned
        for (Comment reply : commentService.findReplies(comment.getId(), null).getContent()) {
            Assert.assertNotNull(reply.getParent());
            Assert.assertEquals(comment.getId(), reply.getParent().getId());
        }

    }

    @Test
    public void commentRepliesAreRemovedWhenParentIsDeleted() throws Exception {
        // CREATE 2 REPLIES FOR A COMMENT
        CommentCreate commentCreate = new CommentCreate();
        commentCreate.setContent("dseqwewq");
        Comment reply1 = commentService.createReply(comment, commentCreate, account);
        commentCreate = new CommentCreate();
        commentCreate.setContent("eq3122");
        Comment reply2 = commentService.createReply(comment, commentCreate, account);
        // CREATE REPLY TO REPLY1
        commentCreate = new CommentCreate();
        commentCreate.setContent("reply to reply");
        Comment replyToReply1 = commentService.createReply(reply1, commentCreate, account);
        // this must return 2 not 3 because findReplies should find only parent replies
        Assert.assertEquals(commentService.findReplies(comment.getId(), null).getContent().size(), 2);
        // reply to reply still exists
        commentService.findById(replyToReply1.getId());
        commentService.delete(comment);
        Assert.assertEquals(commentService.findReplies(comment.getId(), null).getContent().size(), 0);
        // reply to reply must be removed too
        boolean thrown = false;
        try {
            commentService.findById(replyToReply1.getId());
        } catch (NotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void updateComment() throws Exception {
        JSONObject json = new JSONObject();
        String newContent = "dasdae qwewqewq   213  ";
        json.put(fieldCommentContent, newContent);
        mockMvc.perform(put("/comments" + "/" + comment.getId())
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(newContent.trim())));
    }

    @Test
    public void deleteComment() throws Exception {
        mockMvc.perform(delete("/comments" + "/" + comment.getId())
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(delete("/comments" + "/" + comment.getId())
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andExpect(status().isNotFound());
    }

}
