package com.nowcoder.community.utils;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode root = new TrieNode();
    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-wowrks.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            logger.error("添加敏感词失败：" + e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树去
    private void addKeyword(String keyword) {
        TrieNode tempNode = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = root.getSubNode(c);
            if(subNode == null ){
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;

            // 设置结束标识
            if( i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     *  过滤敏感词的方法
     * @param text 待过滤文本
     * @return 过滤后文本
     */
    public String filter(String text) {
        if(StringUtils.isBlank(text)) {
            return null;
        }
        // 指针1 ，指向前缀树的根节点
        TrieNode tempNode = root;
        // 指针2 指向文本，是过滤敏感词的头指针
        int begin = 0;
        // 指针3 指向文本， 是过滤敏感词的尾指针
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);
            // 跳过符号
            if(isSymbol(c)){
                // 指针1 不变，指针2向下走一步
                if(tempNode == root){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            // 下级节点已经为空,上面也没有特殊符号，那么 它（目前截止的字符串） 不是敏感词，将检查字符串的头指针的字符写入
            // 那么需要将前缀树的指针归位根节点，把检查敏感词的头指针跳到下一个位置，尾指针跳到头指针的位置
            if(tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = root;
            }else if(tempNode.isKeywordEnd()){
                // 发现敏感词，将 begin ~ position 字符串 替换
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = root;
            }else {
                // 检查下一个字符串
                position++;
            }
        }
        // 将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c){
        // 0x2E80 ~ 0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树
    private class TrieNode {
        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点（key是下级字符，value是下级节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd(){
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        // 添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }
        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
