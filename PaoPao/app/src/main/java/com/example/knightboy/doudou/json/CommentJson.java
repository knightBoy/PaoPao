package com.example.knightboy.doudou.json;

import com.example.knightboy.doudou.bean.Comment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/9/2.
 */
public class CommentJson {

    public static ArrayList<Comment> jsonToComments(String jsonString){
        ArrayList<Comment> comments = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            //生成新的动态链表
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //生成一条新的评论
                Comment comment = jsonObject2Comment(jsonObject);
                comments.add(comment);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return comments;
    }

    /**
     * 将一个JsonObject对象转换成一条评论
     * @param jsonObject
     * @return
     */
    private static Comment jsonObject2Comment(JSONObject jsonObject){
        Comment comment = new Comment();
        try {
            comment.setId(jsonObject.getInt("id"));
            comment.setStateId(jsonObject.getInt("serialNum"));
            comment.setUserId(jsonObject.getInt("userID"));
            comment.setContents(jsonObject.getString("contents"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return comment;
    }
}
