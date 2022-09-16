package com.jannuzzi.ecultureexperience.data;

import android.os.Build;
import android.os.Environment;
import android.util.JsonReader;

import androidx.annotation.RequiresApi;

import com.jannuzzi.ecultureexperience.data.model.Route;
import com.jannuzzi.ecultureexperience.ui.quiz.Answer;
import com.jannuzzi.ecultureexperience.ui.quiz.Question;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {
    public static InputStream readFile(String Folder, String Name) {
        File file = new File(Environment.getExternalStorageDirectory()
                + "/" + Folder + "/" + Name);
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Path> parsePath(InputStream stream, List<String> idList) throws IOException {
        List<Path> pathList = new ArrayList<>();
        JsonReader reader = new JsonReader(new InputStreamReader(stream));
        reader.beginObject();
        if("id".equals(reader.nextName())) {
            String id = reader.nextString();
            if(id.equals("")) {
                throw new IOException();
            }
            for (String idPath: idList) {
                if(idPath.equals(id)) {
                    return null;
                }
            }
            idList.add(id);
        }
        reader.nextName();
        reader.beginArray();
        while(reader.hasNext()) {
            pathList.add(readPath(reader));
        }
        reader.endArray();
        reader.endObject();
        return pathList;
    }

    private static Path readPath(JsonReader reader) throws IOException {
        String name = "";
        String description = "";
        String tag = "";
        String imagePath = "";
        String path = "";

        reader.beginObject();
        while(reader.hasNext()) {
            String token = reader.nextName();
            if ("name".equals(token)) {
                name = reader.nextString();
            } else if ("description".equals(token)) {
                description = reader.nextString();
            } else if ("tag".equals(token)) {
                tag = reader.nextString();
            } else if ("imagePath".equals(token)) {
                imagePath = reader.nextString();
            } else if ("path".equals(token)) {
                path = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Path(name, description, tag, imagePath, path);
    }

    public static List<Route> parseRoute(InputStream content) {
        JsonReader reader = new JsonReader(new InputStreamReader(content));
        List<Route> instructions = new ArrayList<>();
        try {
            reader.beginArray();

            while (reader.hasNext()) {
                String title = "";
                String description = "";

                reader.beginObject();
                while (reader.hasNext()) {
                    String token = reader.nextName();
                    if ("title".equals(token)) {
                        title = reader.nextString();
                    } else if ("description".equals(token)) {
                        description = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }

                reader.endObject();
                instructions.add(new Route(title, description));
            }

            return instructions;
        } catch (IOException e) {
            e.printStackTrace();
            return instructions;
        }
    }

    public static List<Question> parseQuestions(InputStream content) {
        JsonReader reader = new JsonReader(new InputStreamReader(content));
        List<Question> questions = new ArrayList<>();
        try {

            reader.beginArray();
            while (reader.hasNext()) {
                questions.add(parseSingleQuestion(reader));
            }
            reader.endArray();

            return questions;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Question parseSingleQuestion(JsonReader reader) throws IOException {
        String question = "";
        List<Answer> answers = new ArrayList<>();;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("question")) {
                question = reader.nextString();
            } else if(name.equals("answers")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    Answer answer = parseAnswer(reader);
                    if (answer.isCorrect()) {
                        for (Answer ans: answers) {
                            if(ans.isCorrect())
                                throw new IOException();
                        }
                    }
                    answers.add(answer);

                }
                reader.endArray();
            }
        }
        reader.endObject();

        return new Question(question, answers);
    }

    private static Answer parseAnswer(JsonReader reader) throws IOException {
        String text = "";
        boolean correct = false;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("text")) {
                text = reader.nextString();
            } else if(name.equals("correct")) {
                correct = reader.nextBoolean();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Answer(text, correct);
    }
}
