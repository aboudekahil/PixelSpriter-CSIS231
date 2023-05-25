package com.abdelkader.backend;

import com.abdelkader.backend.modals.UserDTO;

public class UserHandler {
    private static UserDTO currUser = null;

    public static UserDTO getCurrUser() {
        return currUser;
    }

    public static void setCurrUser(UserDTO currUser) {
        UserHandler.currUser = currUser;
    }
}
