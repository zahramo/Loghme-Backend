package ir.ac.ut.ece.ie.loghme.service;

import ir.ac.ut.ece.ie.loghme.Jwt;
import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.user.UserMapper;
import ir.ac.ut.ece.ie.loghme.repository.exception.NotEnoughPartyFood;
import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;
import ir.ac.ut.ece.ie.loghme.repository.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RestController
public class AuthenticationService {
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity signup(@RequestParam("firstName") String firstName,
                                 @RequestParam("lastName") String lastName,
                                 @RequestParam("phoneNumber") String phoneNumber,
                                 @RequestParam("email") String email,
                                 @RequestParam("password") String password) {
        System.out.println("Request For Signup Service.");
        try{
            if(!LoghmeRepository.getCurInstance().userExists(email)) {
                LoghmeRepository.getCurInstance().addUserToDb(firstName, lastName, phoneNumber, email, password);
                return ResponseEntity.ok("ثبت نام شما با موفقیت انجام شد");
            }
            else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("کاربری با این ایمیل قبلا ثبت نام کرده است.");
            }
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("مشکلی در پایگاه داده پیش آمده‌است.");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestParam("email") String email,
                                 @RequestParam("password") String password) {
        System.out.println("Request For Login Service.");
        int id = LoghmeRepository.getCurInstance().findUser(email, password);
//        System.out.println("1");
        if(id > 0) {
            String jwt = Jwt.getCurInstance().create(String.valueOf(id), "Loghme", "auth-login", 24);
//            System.out.println("2");
            System.out.println("Jwt: " + jwt);
            System.out.println("User id: " + id);
            return ResponseEntity.ok(new ArrayList<String>(Arrays.asList(jwt)));
        }
        else {
//            System.out.println("3");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("رمز عبور یا ایمیل وارد شده صحیح نیست.");
        }
    }

    @RequestMapping(value = "/googleLogin", method = RequestMethod.POST)
    public ResponseEntity login(@RequestParam("email") String email) {
        System.out.println("Request For Google Login Service.");
        System.out.println("From: "+ email);
        try {
            User user = UserMapper.getInstance().find(new ArrayList<>(Collections.singletonList(email)));
            if(user != null){
                String jwt = Jwt.getCurInstance().create(String.valueOf(user.getId()),
                        "Loghme", "auth-login", 24);
                System.out.println("Jwt: " + jwt);
                System.out.println("User id: " + user.getId());
                return ResponseEntity.ok(new ArrayList<String>(Arrays.asList(jwt)));
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("کاربری با این اکانت گوگل ثبت نام نکرده است.");
            }
        }catch (SQLException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("مشکلی در پایگاه داده پیش آمده‌است.");
        }
    }
}
