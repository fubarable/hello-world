package foo2;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

/**
 * http://stackoverflow.com/a/41429459/522444
 * @author Pete
 *
 */
@SuppressWarnings("serial")
public class EventDrivenEx extends JPanel {
    private static void createAndShowGui() {
        JFrame frame = new JFrame("EventDrivenEx");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new EventDrivenEx());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGui());
    }
}

@SuppressWarnings("serial")
class QuestionsPanel extends JPanel {
    private List<QuestionPanel> qPanelList = new ArrayList<>();
    private int qPanelIndex = 0;
    
}

@SuppressWarnings("serial")
class QuestionPanel extends JPanel {
    private Question question;
    private ButtonGroup buttonGroup = new ButtonGroup();
    private boolean solved = false;

    public QuestionPanel(Question question) {
        this.question = question;
        JLabel questionLabel = new JLabel("Question:" + question.getQuestion(), SwingConstants.CENTER);       
        JPanel answersPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        answersPanel.setBorder(BorderFactory.createTitledBorder("Select an Answer"));
        for (String possibleAnswer : question.getAllAnswersShuffled()) {
            JRadioButton rBtn = new JRadioButton(possibleAnswer);
            rBtn.setActionCommand(possibleAnswer);
            answersPanel.add(rBtn);
            buttonGroup.add(rBtn);
        }
        
        setLayout(new BorderLayout());
        add(questionLabel, BorderLayout.PAGE_START);
        add(answersPanel, BorderLayout.CENTER);
    }
    
    public String getSelectedAnswer() throws QuestionPanelException {
        ButtonModel buttonModel = buttonGroup.getSelection();
        if (buttonModel == null) {
            String message = "No answer selected";
            throw new QuestionPanelException(message);
        }
        return buttonModel.getActionCommand();
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setSolved(boolean solved) {
        this.solved = solved;
    }
    
    public boolean isSolved() {
        return solved;
    }

}

@SuppressWarnings("serial")
class QuestionPanelException extends Exception {

    public QuestionPanelException() {
        super();
    }

    public QuestionPanelException(String message) {
        super(message);
    }
    
}

class Question {
    private String question;
    private String correctAnswer;
    private List<String> wrongAnswers;

    public Question(String question, String correctAnswer) {
        this.question = question;
        this.correctAnswer = correctAnswer;
    }

    public void addWrongAnswers(String text) {
        wrongAnswers.add(text);
    }

    public List<String> getAllAnswersShuffled() {
        List<String> allAnswers = new ArrayList<>(wrongAnswers);
        allAnswers.add(correctAnswer);
        Collections.shuffle(allAnswers);
        return allAnswers;
    }

    public boolean checkAnswer(String test) {
        return correctAnswer.equalsIgnoreCase(test);
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getQuestion() {
        return question;
    }
}
