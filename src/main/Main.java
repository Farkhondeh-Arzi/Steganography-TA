package main;

import main.stegano.EmailContext;

public class Main {
    public static void main(String[] args) {

        String secretMessage = "Behind using";
        String coverMessage = " School of Electrical and Computer Engineering " +
                "(ECE), initiated in 1934, now offers " +
                "undergraduate, masters, and PhD degrees in " +
                "Electrical Engineering, Computer Engineering, " +
                "and Information Technology. It is the pioneer of " +
                "all higher education centers in the field of " +
                "electrical and computer engineering nationwide. " +
                "With more than 2000 students, 84 faculty " +
                "members, including 21 Professors, 20 Associate " +
                "Professors, 40 Assistant Professors, and three " +
                "lectures. The School of ECE faculties earn " +
                "teaching ratings that are among the best in the " +
                "College of Engineering, and also at the University" +
                "of Tehran.";

        EmailContext emailContext = new EmailContext(coverMessage);

        EMailSteganography eMailSteganography = new EMailSteganography(emailContext);
        eMailSteganography.embedMessage(secretMessage, coverMessage);
    }
}