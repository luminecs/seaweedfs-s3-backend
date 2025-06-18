package com.yourcompany.seaweedfs.model;

import java.util.List;

public record Identity(String name, List<Credential> credentials, List<String> actions) {
}
