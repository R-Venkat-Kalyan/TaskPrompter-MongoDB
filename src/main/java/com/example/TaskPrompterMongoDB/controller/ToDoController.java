package com.example.TaskPrompterMongoDB.controller;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.TaskPrompterMongoDB.entity.ContactEntity;
import com.example.TaskPrompterMongoDB.entity.ToDoEntity;
import com.example.TaskPrompterMongoDB.entity.UserEntity;
import com.example.TaskPrompterMongoDB.service.ContactService;
import com.example.TaskPrompterMongoDB.service.ToDoService;
import com.example.TaskPrompterMongoDB.service.UserService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ToDoController {

	@Autowired
	private ToDoService tds;

	@Autowired
	private UserService us;

	@Autowired
	private ContactService cs;

	@Autowired
	private JavaMailSender mailSender;

	@GetMapping("/")
	public String Home() {
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "Login";
	}

	@GetMapping("/register")
	public String register() {
		return "Register";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	@GetMapping("/error")
	public String Error() {
		return "Error";
	}

	@PostMapping("/save")
	public String registerUser(@ModelAttribute UserEntity user, HttpSession session) {
		// Save user details in session
		session.setAttribute("user", user);
		// Generate OTP and send email
		String regEmail = user.getUserMail();
		sendOTP(regEmail, session);
		return "redirect:/OTPForm";
	}

	@GetMapping("/OTPForm")
	public String showOTPForm() {
		return "OTPVerification";
	}

	@GetMapping("/verify")
	public String verifyUser(@RequestParam("userOTP") int userOTP, HttpSession session,
			RedirectAttributes redirectAttributes) {
		int generatedOTP = (int) session.getAttribute("generatedOTP");
//	    System.out.println(userOTP+" "+generatedOTP);
		if (userOTP == generatedOTP) {
			UserEntity user = (UserEntity) session.getAttribute("user");
			us.registerUser(user);
			session.invalidate(); // Clear session after successful registration
			redirectAttributes.addFlashAttribute("successMessage", "User registered successfully..✔\nLogin Now");
			return "redirect:/login";
		} else {
			redirectAttributes.addFlashAttribute("successMessage", "Invalid OTP..❌❌");
			return "redirect:/register"; // Redirect back to registration if OTP doesn't match
		}
	}

	private void sendOTP(String regEmail, HttpSession session) {
		// Code to send OTP via email using mailsender
		String from = "taskprompter@gmail.com";
		String to = regEmail;
		int generatedOTP = generateRandomOTP();
		session.setAttribute("generatedOTP", generatedOTP);
		try {
			// Read HTML email template
			Resource resource = new ClassPathResource("templates/Mail.html");
			String emailTemplate = new String(Files.readAllBytes(resource.getFile().toPath()));

			// Replace placeholder with generated OTP
			String emailBody = emailTemplate.replace("{{otp}}", String.valueOf(generatedOTP));

			// Create MimeMessage
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject("Verify email address for TaskPrompter.com");
			helper.setText(emailBody, true);

			// Send email
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	private int generateRandomOTP() {
		Random random = new Random();
		return 100000 + random.nextInt(900000);
	}

	@PostMapping("/userLogin")
	public String userPanel(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model,
			HttpSession session) {
		List<UserEntity> allUsers = us.getAllUsers();
		boolean userFound = false;
		for (UserEntity ue : allUsers) {
			String pwd = request.getParameter("password");
			String email = request.getParameter("username");
			if (ue.getUserMail().equals(email) && ue.getPassword().equals(pwd)) {
				model.addAttribute("userName", ue.getUserName());
				session.setAttribute("email", email);
				session.setAttribute("userName", ue.getUserName());
				session.setMaxInactiveInterval(30 * 60);
				userFound = true;
				return "redirect:/user";
			} else if (ue.getUserMail().equals(email) && !ue.getPassword().equals(pwd)) {
				redirectAttributes.addFlashAttribute("successMessage", "Invalid Password..❌❌");
				return "redirect:/login";
			}
		}

		if (!userFound) {
			redirectAttributes.addFlashAttribute("successMessage", "User Not Found..❌\nPlease Register First");
			return "redirect:/register";
		}

		redirectAttributes.addFlashAttribute("successMessage", "Invalid Credentials..❌❌");
		return "redirect:/login";
	}

	@GetMapping("/user")
	public String dashboard(HttpSession session, Model model) {
		if (session != null && session.getAttribute("email") != null) {
			String userName = (String) session.getAttribute("userName");
			model.addAttribute("userName", userName);
			return "DashBoard";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/contact")
	public String addcontact(@ModelAttribute ContactEntity ce) {
		cs.saveContact(ce);
		return "redirect:/";
	}

	@GetMapping("/addTask")
	public String addTask(HttpSession session, Model model) {
		if (session != null && session.getAttribute("email") != null) {
			String userName = (String) session.getAttribute("userName");
			model.addAttribute("userName", userName);
			return "AddTask";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/saveTask")
	public String saveTask(@ModelAttribute ToDoEntity tde, HttpSession session, RedirectAttributes redirectAttributes) {
		if (session != null && session.getAttribute("email") != null) {
			String email1 = (String) session.getAttribute("email");
			tde.setEmail(email1);
			tds.saveTask(tde);
			redirectAttributes.addFlashAttribute("successMessage", "Task added Successfully..✔");
			return "redirect:/addTask";
		} else {
			return "redirect:/login";
		}
	}

	private Set<ToDoEntity> getUserTasks(HttpSession session) {
		String email1 = (String) session.getAttribute("email");
		Set<ToDoEntity> userTasks = new HashSet<>();
		List<ToDoEntity> allTasks = tds.allTasks();

		for (ToDoEntity te : allTasks) {
			if (te.getEmail() != null && te.getEmail().equals(email1)) {
				userTasks.add(te);
			}
		}

		return userTasks;
	}

	@GetMapping("/tasks")
	public ModelAndView getTaskList(HttpSession session, Model model) {
		if (session.getAttribute("email") == null) {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("redirect:/login");
			return modelAndView;
		}
		Set<ToDoEntity> userTasks = getUserTasks(session);
		String userName = (String) session.getAttribute("userName");
		model.addAttribute("userName", userName);
		return new ModelAndView("TasksList", "lists", userTasks);
	}

	@GetMapping("/editTasks")
	public ModelAndView editTaskList(HttpSession session, Model model) {
		if (session.getAttribute("email") == null) {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("redirect:/login");
			return modelAndView;
		}
		Set<ToDoEntity> userTasks = getUserTasks(session);
		String userName = (String) session.getAttribute("userName");
		model.addAttribute("userName", userName);
		return new ModelAndView("EditTasks", "lists", userTasks);
	}

	@RequestMapping("/deleteTask/{id}")
	public String deleteTask(@PathVariable("id") String id, RedirectAttributes redirectAttributes,
			HttpSession session) {
		if (session.getAttribute("email") == null) {
			return "redirect:/login";
		}
		ToDoEntity task = tds.getByTask(id);
		redirectAttributes.addFlashAttribute("successMessage", "Task: " + task.getText() + " has been deleted ❗❗");
		tds.deleteTask(id);
		return "redirect:/editTasks";
	}

	@GetMapping("/updateTask/{id}")
	public String showUpdateTaskForm(@PathVariable("id") String id, Model model, HttpSession session) {
		if (session.getAttribute("email") == null) {
			return "redirect:/login";
		}
		ToDoEntity td = tds.getByTask(id);
		if (td != null) {
			String userName = (String) session.getAttribute("userName");
			model.addAttribute("userName", userName);
			model.addAttribute("list", td);
			session.setAttribute("tid", id);
			return "UpdateTask";
		} else {
			return "Error";
		}
	}

	@PostMapping("/updateTask")
	public String updateTask(@ModelAttribute("list") ToDoEntity todo, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (session.getAttribute("email") == null) {
			return "redirect:/login";
		}
		String email1 = (String) session.getAttribute("email");
		String id = (String) session.getAttribute("tid");
		todo.setId(id);
		todo.setEmail(email1);
		todo.setStatus("Not Notified");
		tds.saveTask(todo);
		redirectAttributes.addFlashAttribute("successMessage", "Task: " + todo.getText() + " has been updated ✏✏");
		return "redirect:/tasks";
	}

	@GetMapping("/profile")
	public String getProfile(HttpSession session) {
		if (session.getAttribute("email") == null) {
			return "redirect:/login";
		}
		return "profile";
	}

	@GetMapping("/updateProfile")
	public String updateProfile(Model model, HttpSession session) {
		if (session.getAttribute("email") == null) {
			return "redirect:/login";
		}
		String email = (String) session.getAttribute("email");
		UserEntity td = us.getByEmail(email);
		model.addAttribute("list", td);
		return "profile";
	}

	@PostMapping("/update")
	public String updateUser(@ModelAttribute("list") UserEntity user, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (session.getAttribute("email") == null) {
			return "redirect:/login";
		}
		String email1 = (String) session.getAttribute("email");
		user.setUserMail(email1);
		us.registerUser(user);
		redirectAttributes.addFlashAttribute("successMessage",
				"User Updated Successfully..✔\nPlease login with Updated Credentials");
		session.invalidate();
		return "redirect:/login";
	}

}
