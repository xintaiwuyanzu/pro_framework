package com.dr;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ElasticSearchTest {
    Node node;
    Client client;

    @Before
    public void init() throws Exception {
        URL resource1 = getClass().getClassLoader().getResource("elasticsearch.yml");
        Path path = Paths.get(resource1.toURI());
        Settings setting = Settings.builder().loadFromPath(path).build();
        Environment environment = new Environment(setting, path);
        //node = new Node(environment);
        client = node.client();
    }

    @Test
    public void createIndex() {
        client.admin().indices().create(new CreateIndexRequest("aaaa"));
    }

    @After
    public void close() throws IOException {
        if (node != null) {
            node.close();
        }
    }

}
