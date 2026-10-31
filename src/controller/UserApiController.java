package controller;

import framework.JsonBuilder;
import framework.annotation.ApiController;
import framework.annotation.GetMapping;
import framework.annotation.PostMapping;
import java.util.Arrays;

@ApiController
public class UserApiController {

    @GetMapping("/api/users")
    public String listUsers() {
        return new JsonBuilder()
            .put("status", "success")
            .put("data", Arrays.asList("Carole", "Nivah", "Charline"))
            .put("message", "Nom d'utilisateurs récupérés avec succès")
            .toString();
    }

    @GetMapping("/api/users/count")
    public String getUserCount() {
        return new JsonBuilder()
            .put("status", "success")
            .put("count", 3)
            .put("message", "Total de nombre d'utilisateurs récupérés avec succès")
            .toString();
    }

    @PostMapping("/api/users")
    public String createUser() {
        return new JsonBuilder()
            .put("status", "success")
            .put("id", 4)
            .put("message", "User created successfully")
            .toString();
    }
}