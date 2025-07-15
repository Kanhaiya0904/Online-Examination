import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class OnlineExamSystem extends JFrame {
    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);

    String currentUser = "user";
    String currentPass = "pass";

    String selectedSubject = "";
    int currentQuestion = 0;
    int score = 0;
    int timeLeft = 60; // seconds
    javax.swing.Timer timer;

    JLabel timerLabel = new JLabel("Time Left: 60");
    JLabel questionLabel = new JLabel();
    JRadioButton[] options = new JRadioButton[4];
    ButtonGroup optionGroup = new ButtonGroup();
    JButton nextButton = new JButton("Next");
    JButton submitButton = new JButton("Submit");

    Map<String, String[][]> subjects = new HashMap<>();

    String[][] questions;

    public OnlineExamSystem() {
        setTitle("Online Exam System");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(mainPanel);

        addLoginPanel();
        addUpdatePanel();
        addSubjectPanel();
        addQuizPanel();
        addResultPanel();

        loadQuestions();

        setVisible(true);
    }

    void addLoginPanel() {
        JPanel login = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JButton updateBtn = new JButton("Update Profile");

        login.add(new JLabel("Username:"));
        login.add(userField);
        login.add(new JLabel("Password:"));
        login.add(passField);
        login.add(loginBtn);
        login.add(updateBtn);

        loginBtn.addActionListener(e -> {
            if (userField.getText().equals(currentUser) &&
                String.valueOf(passField.getPassword()).equals(currentPass)) {
                cardLayout.show(mainPanel, "subject");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials");
            }
        });

        updateBtn.addActionListener(e -> cardLayout.show(mainPanel, "update"));

        mainPanel.add(login, "login");
    }

    void addUpdatePanel() {
        JPanel update = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField newUser = new JTextField();
        JPasswordField newPass = new JPasswordField();
        JButton save = new JButton("Save");
        JButton back = new JButton("Back");

        update.add(new JLabel("New Username:"));
        update.add(newUser);
        update.add(new JLabel("New Password:"));
        update.add(newPass);
        update.add(save);
        update.add(back);

        save.addActionListener(e -> {
            currentUser = newUser.getText();
            currentPass = String.valueOf(newPass.getPassword());
            JOptionPane.showMessageDialog(this, "Updated Successfully");
            cardLayout.show(mainPanel, "login");
        });

        back.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        mainPanel.add(update, "update");
    }

    void addSubjectPanel() {
        JPanel subject = new JPanel(new GridLayout(4, 2, 10, 10));
        String[] subs = {"English", "Maths", "History", "Physics", "Chemistry", "Biology", "Politics"};
        for (String sub : subs) {
            JButton b = new JButton(sub);
            subject.add(b);
            b.addActionListener(e -> {
                selectedSubject = sub;
                questions = subjects.get(sub);
                currentQuestion = 0;
                score = 0;
                startQuiz();
                cardLayout.show(mainPanel, "quiz");
            });
        }
        mainPanel.add(subject, "subject");
    }

    void addQuizPanel() {
        JPanel quiz = new JPanel(new BorderLayout(10, 10));
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(timerLabel);
        top.add(questionLabel);

        JPanel center = new JPanel(new GridLayout(4, 1));
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            optionGroup.add(options[i]);
            center.add(options[i]);
        }

        JPanel bottom = new JPanel();
        bottom.add(nextButton);
        bottom.add(submitButton);

        quiz.add(top, BorderLayout.NORTH);
        quiz.add(center, BorderLayout.CENTER);
        quiz.add(bottom, BorderLayout.SOUTH);

        nextButton.addActionListener(e -> {
            checkAnswer();
            currentQuestion++;
            if (currentQuestion < 10) {
                loadQuestion(currentQuestion);
            } else {
                submitQuiz();
            }
        });

        submitButton.addActionListener(e -> submitQuiz());

        mainPanel.add(quiz, "quiz");
    }

    void addResultPanel() {
        JPanel result = new JPanel(new BorderLayout());
        JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
        JButton logout = new JButton("Logout");

        logout.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        result.add(resultLabel, BorderLayout.CENTER);
        result.add(logout, BorderLayout.SOUTH);

        mainPanel.add(result, "result");

        submitButton.addActionListener(e -> {
            resultLabel.setText("Your Score: " + score + "/10");
            cardLayout.show(mainPanel, "result");
        });
    }

    void loadQuestions() {
        String[][] english = {
            {"Synonym of 'Big'?", "Tiny", "Huge", "Small", "Little", "Huge"},
            {"Antonym of 'Good'?", "Bad", "Nice", "Sweet", "Right", "Bad"},
            {"Verb in 'He runs fast'?", "He", "Runs", "Fast", "None", "Runs"},
            {"Noun in 'The cat slept.'?", "The", "Slept", "Cat", "None", "Cat"},
            {"Opposite of 'Cold'?", "Warm", "Hot", "Cool", "Chilly", "Hot"},
            {"Choose article: ___ apple", "A", "An", "The", "None", "An"},
            {"Correct Spelling?", "Recieve", "Receive", "Recive", "Receeve", "Receive"},
            {"She ___ singing.", "Is", "Are", "Am", "Were", "Is"},
            {"Plural of 'Mouse'?", "Mice", "Mouses", "Mices", "Mouse", "Mice"},
            {"Past of 'Go'?", "Went", "Gone", "Goes", "Go", "Went"}
        };

        String[][] maths = {
            {"2 + 2 = ?", "2", "4", "6", "8", "4"},
            {"Square root of 9?", "1", "2", "3", "4", "3"},
            {"5 * 6 = ?", "30", "60", "15", "25", "30"},
            {"10 / 2 = ?", "5", "2", "10", "20", "5"},
            {"15 - 7 = ?", "8", "7", "6", "9", "8"},
            {"What is 12 % 5?", "2", "1", "0", "3", "2"},
            {"100 / 10 = ?", "10", "20", "5", "1", "10"},
            {"25 + 17 = ?", "42", "43", "40", "44", "42"},
            {"8 * 7 = ?", "56", "48", "64", "72", "56"},
            {"Square of 5?", "10", "25", "15", "20", "25"}
        };

        String[][] history = {
            {"Who was the first President of India?", "Rajendra Prasad", "Nehru", "Gandhi", "Patel", "Rajendra Prasad"},
            {"Who discovered India?", "Columbus", "Vasco da Gama", "Cook", "Newton", "Vasco da Gama"},
            {"Which year did India get independence?", "1947", "1950", "1935", "1960", "1947"},
            {"Who was the Father of the Nation?", "Bhagat Singh", "Gandhi", "Bose", "Nehru", "Gandhi"},
            {"Who led the Salt March?", "Tilak", "Gandhi", "Bose", "Ambedkar", "Gandhi"},
            {"Capital of Maurya Empire?", "Delhi", "Pataliputra", "Kolkata", "Bombay", "Pataliputra"},
            {"Battle of Plassey year?", "1757", "1857", "1657", "1957", "1757"},
            {"Quit India Movement year?", "1942", "1947", "1930", "1950", "1942"},
            {"Who founded the Mughal Empire?", "Babur", "Akbar", "Shah Jahan", "Aurangzeb", "Babur"},
            {"Taj Mahal was built by?", "Akbar", "Babur", "Shah Jahan", "Aurangzeb", "Shah Jahan"}
        };

        String[][] physics = {
        {"Unit of Force?", "Newton", "Joule", "Watt", "Ohm", "Newton"},
        {"Speed = ?", "Distance/Time", "Mass*Velocity", "Work*Time", "Force/Area", "Distance/Time"},
        {"Unit of Power?", "Watt", "Joule", "Newton", "Ampere", "Watt"},
        {"Light travels in?", "Straight line", "Circle", "Zigzag", "Random", "Straight line"},
        {"Gravity discovered by?", "Newton", "Einstein", "Tesla", "Faraday", "Newton"},
        {"SI unit of energy?", "Watt", "Joule", "Calorie", "Ohm", "Joule"},
        {"Sound needs medium?", "Yes", "No", "Sometimes", "Never", "Yes"},
        {"Velocity unit?", "m/s", "kg", "m", "s", "m/s"},
        {"Resistance unit?", "Ohm", "Volt", "Ampere", "Watt", "Ohm"},
        {"Mass x Acceleration = ?", "Force", "Power", "Work", "Energy", "Force"}
    };

    String[][] chemistry = {
        {"Atomic number of Hydrogen?", "1", "2", "3", "4", "1"},
        {"H2O is?", "Oxygen", "Water", "Hydrogen", "Acid", "Water"},
        {"Symbol for Sodium?", "Na", "S", "So", "N", "Na"},
        {"Gas in balloons?", "Helium", "Hydrogen", "Oxygen", "Nitrogen", "Helium"},
        {"PH value of water?", "7", "1", "10", "14", "7"},
        {"Salt formula?", "NaCl", "KCl", "HCl", "MgCl", "NaCl"},
        {"Acid in lemon?", "Citric", "Acetic", "Sulfuric", "Hydrochloric", "Citric"},
        {"Atomic number of Oxygen?", "8", "16", "6", "10", "8"},
        {"Na + Cl = ?", "NaCl", "KCl", "HCl", "NaOH", "NaCl"},
        {"Metal that is liquid?", "Mercury", "Iron", "Copper", "Aluminium", "Mercury"}
    };

    String[][] biology = {
        {"Basic unit of life?", "Cell", "Tissue", "Organ", "Nucleus", "Cell"},
        {"Largest organ?", "Skin", "Liver", "Heart", "Lungs", "Skin"},
        {"Photosynthesis needs?", "CO2", "O2", "N2", "H2", "CO2"},
        {"Blood transports?", "Oxygen", "CO2", "Hormones", "All", "All"},
        {"RBCs carry?", "Oxygen", "CO2", "Waste", "Water", "Oxygen"},
        {"DNA is in?", "Nucleus", "Cell wall", "Cytoplasm", "Membrane", "Nucleus"},
        {"Insulin controls?", "Sugar", "Blood", "Protein", "Fat", "Sugar"},
        {"Plant food is made by?", "Leaves", "Roots", "Stem", "Fruit", "Leaves"},
        {"Organ for thinking?", "Brain", "Heart", "Liver", "Lungs", "Brain"},
        {"Human has how many bones?", "206", "208", "210", "205", "206"}
    };

    String[][] politics = {
        {"Head of India?", "President", "PM", "CM", "Governor", "President"},
        {"Current PM (2025)?", "Modi", "Rahul", "Shah", "Yogi", "Modi"},
        {"No. of Lok Sabha seats?", "545", "250", "200", "100", "545"},
        {"Election Commission is?", "Independent", "Govt", "Private", "Military", "Independent"},
        {"MLA stands for?", "Member of Legislative Assembly", "Minister", "Law Agent", "None", "Member of Legislative Assembly"},
        {"Who signs bills?", "President", "PM", "CM", "Speaker", "President"},
        {"Who appoints Governor?", "President", "PM", "CM", "People", "President"},
        {"Rajya Sabha term?", "6 years", "5 years", "4 years", "3 years", "6 years"},
        {"Who is Chief Justice?", "Head of Judiciary", "President", "Lawyer", "Minister", "Head of Judiciary"},
        {"Supreme law of India?", "Constitution", "Parliament", "Court", "Order", "Constitution"}
    };
        subjects.put("English", english);
        subjects.put("Maths", maths);
        subjects.put("History", history);
        subjects.put("Physics", history);
        subjects.put("Chemistry", maths);
        subjects.put("Biology", english);
        subjects.put("Politics", history);
    }

    void startQuiz() {
        loadQuestion(0);
        timeLeft = 60;
        timerLabel.setText("Time Left: 60");
        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time Left: " + timeLeft);
            if (timeLeft <= 0) {
                timer.stop();
                submitQuiz();
            }
        });
        timer.start();
    }

    void loadQuestion(int index) {
        optionGroup.clearSelection();
        questionLabel.setText((index + 1) + ". " + questions[index][0]);
        for (int i = 0; i < 4; i++) {
            options[i].setText(questions[index][i + 1]);
        }
    }

    void checkAnswer() {
        for (int i = 0; i < 4; i++) {
            if (options[i].isSelected() &&
                options[i].getText().equals(questions[currentQuestion][5])) {
                score++;
            }
        }
    }

    void submitQuiz() {
        if (timer != null) timer.stop();
        checkAnswer();
        JLabel resultLabel = (JLabel) ((JPanel) mainPanel.getComponent(4)).getComponent(0);
        resultLabel.setText("Your Score: " + score + "/10");
        cardLayout.show(mainPanel, "result");
    }

    public static void main(String[] args) {
        new OnlineExamSystem();
    }
}
