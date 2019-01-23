package com.gpch.login.controller;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.gpch.login.model.Role;
import com.gpch.login.model.User;
import com.gpch.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController implements ErrorController{

    @Autowired
    private UserService userService;

    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }


    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult,
            @RequestParam("passTwo") String passTwo) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            if(user.getPassword().equals(passTwo) == true){
                userService.saveUser(user);
                modelAndView.addObject("successMessage", "User has been registered successfully");
                modelAndView.addObject("user", new User());
            }
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }
    /*=======================================
          ADMIN homepage showcase
      =======================================*/
    @RequestMapping(value="/admin/home", method = RequestMethod.GET)
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName",user.getName() + " " + user.getLastName());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }
    /*=======================================
       ADMINISTRATION TOOLS
      =======================================*/

    @RequestMapping(value = "/admin/edituser", method = RequestMethod.GET)
    public ModelAndView adminToolsEditUser(@RequestParam("email") String email){
        String userRole = new String();
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findUserByEmail(email);
        modelAndView.addObject(user);
        modelAndView.setViewName("/admin/edituser"); // 88 - 92 za uvatit koji mu je role, String
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            userRole = role.getRole();           
        }
        modelAndView.addObject("role", userRole);
        return modelAndView;
    }
    //administration tools all users
    @RequestMapping(value = "/admin/listall", method = RequestMethod.GET)
    public ModelAndView listAllUsers() {
        ModelAndView modelAndView = new ModelAndView();
        List<User> users = userService.findAllUsers();
        modelAndView.addObject("listOfUsers", users);
        modelAndView.setViewName("/admin/listall");
        return modelAndView;
    }
    //administration tools give/remove admin
    @RequestMapping(value = "/admin/editrole", method = RequestMethod.GET)
    public String editUserRole(@RequestParam("givenRole") String givenRole, 
    @RequestParam("email") String email){
        if(givenRole.equals("giveADMIN") == true){
            userService.updateRole(email,"ADMIN");
        }
        else if(givenRole.equals("removeADMIN") == true){
            userService.updateRole(email, "USER");
        }
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals(email) == true) {
            return "redirect:/logout"; // slucaj da sam sebi mices admin role da te redirecta na root
        } 
        else {
            return "redirect:../admin/listall"; // ostalim userima si minja role
        }
    }
    //administration tools enable/disable account
    @RequestMapping(value = "/admin/offonaccount", method = RequestMethod.GET)
    public String editAccountOnOff(@RequestParam("givenActive") String givenActive,
    @RequestParam("email") String email){
        if (givenActive.equals("ActiveOff") == true){
            userService.updateActive(email, 0);
        }
        else if (givenActive.equals("ActiveOn") == true){
            userService.updateActive(email, 1);
        }
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals(email) == true) {
            return "redirect:/logout"; // slucaj da sam sebi gasis acc da te redirecta na root
        }
        else {
            return "redirect:../admin/listall"; // ostalim userima si minja active
        }
    }

    /*=======================================
          user homepage showcase, or redirect to ADMIN homepage
      =======================================*/
    @RequestMapping(value = "/user/home", method = RequestMethod.GET)
    public ModelAndView userHome() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (this.checkIfAdmin(auth)) {
            modelAndView.setViewName("redirect:/admin/home");
        }
        else{
            User user = userService.findUserByEmail(auth.getName());
            modelAndView.addObject("userName", user.getName() + " " + user.getLastName());
            modelAndView.setViewName("user/home");
        }
        return modelAndView;
    }
    
    /*=======================================
          user profile showcase
      =======================================*/
    @RequestMapping(value = { "/user/myprofile", "/admin/myprofile"}, method = RequestMethod.GET)
    public ModelAndView userProfileView() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName", user.getName());
        modelAndView.addObject("userLastName", user.getLastName());
        modelAndView.addObject("userEmail", user.getEmail());
        if (this.checkIfAdmin(auth)) {
            modelAndView.setViewName("admin/myprofile");
        }
        else{
            modelAndView.setViewName("user/myprofile");
        }
        
        return modelAndView;
    }
    /*=======================================
          user edit profile showcase
      =======================================*/
    @RequestMapping(value = { "/user/editprofile", "/admin/editprofile"}, method = RequestMethod.GET)
    public ModelAndView userEditProfileView() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName", user.getName());
        modelAndView.addObject("userLastName", user.getLastName());
        modelAndView.addObject("userEmail", user.getEmail());
        if (this.checkIfAdmin(auth)) {
            modelAndView.setViewName("admin/editprofile");
        } else {
            modelAndView.setViewName("user/editprofile");
        }
        return modelAndView;
    }
    //user edit name
    @RequestMapping(value = {
        "/user/editprofile/edit_name","/admin/editprofile/edit_name"},
        method = RequestMethod.POST)
    public String editUserName(@RequestParam("name") String name){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userService.updateUserName(auth.getName(), name);
        return "redirect:../editprofile";
    }

    // user edit last name
    @RequestMapping(value = {
        "/user/editprofile/edit_lastName", "/admin/editprofile/edit_lastName"},
        method = RequestMethod.POST)
    public String editUserLastName(@RequestParam("lastName") String lastName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userService.updateUserLastName(auth.getName(), lastName);
        return "redirect:../editprofile";
    }
    
    //user edit password
    @RequestMapping(value = {
        "/user/editprofile/edit_password", "/admin/editprofile/edit_password"}, 
        method = RequestMethod.POST)
    public ModelAndView editUserPassword(
        @RequestParam("passOne") String passOne, @RequestParam("passTwo") String passTwo) {
            ModelAndView modelAndView = new ModelAndView();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(passOne.equals(passTwo) != true){
                modelAndView.addObject("errorPassword","Passwords don't match!");
            }
            else{
                userService.updateUserPassword(auth.getName(), passOne);
                modelAndView.addObject("errorPassword", "Your password was changed successfully.");
            }
            User user = userService.findUserByEmail(auth.getName());
            modelAndView.addObject("userName", user.getName());
            modelAndView.addObject("userLastName", user.getLastName());
            modelAndView.addObject("userEmail", user.getEmail());
            if (this.checkIfAdmin(auth)) {
                modelAndView.setViewName("admin/editprofile");
            } 
            else {
                modelAndView.setViewName("user/editprofile");
            }
            return modelAndView;
    }
    /*=======================================
          user delete showcase
      =======================================*/
    @RequestMapping(value = {
        "/user/delprofile", "/admin/delprofile"},
        method = RequestMethod.GET)
    public ModelAndView userDeleteView(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName", user.getName());
        if (this.checkIfAdmin(auth)) {
            modelAndView.setViewName("admin/deleteprofile");
        } else {
            modelAndView.setViewName("user/deleteprofile");
        }
        return modelAndView;
    }
    // delete user (seta se active u 0 i ka da ga nema a svi podaci sacuvani kakogod)
    @RequestMapping(value = {
        "/user/delprofile/deleteUser", "/admin/delprofile/deleteUser"},
        method = RequestMethod.POST)
    public String deleteUser(@RequestParam("delete") String confirmation){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (confirmation.equals("YES")){
            userService.setZeroUserActive(auth.getName());
            return "redirect:/?logout";
        }
        else{
            return "redirect:../home";
        }
    }



  
//redirect za usere ca od admin role stranica
    @Override
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String getErrorPath() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userService.findUserByEmail(auth.getName()) == null){
            return "redirect:../login";
        }
        else{
            return "redirect:../user/home";
        }
    }
    
// shortcut functions
    private boolean checkIfAdmin(Authentication auth){
        return auth.getAuthorities().toString().equals("[ADMIN]");
    }

}



