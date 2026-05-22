package com.seap;

import org.springframework.ai.vectorstore.VectorStore;

public class SpringAiVectorStore {

    private final VectorStore vectorStore;

    public SpringAiVectorStore(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


}
