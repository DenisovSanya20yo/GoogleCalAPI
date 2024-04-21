import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        String calendarId = "denisovalexandr1000@gmail.com";

        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("C:\\Users\\Admin\\Downloads\\calproject-420912-d3c7cc18401a.json"))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/calendar.events"));

        Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("My Application")
                .build();

        Scanner scanner = new Scanner(System.in);
        String action = "";

        while (!action.equals("1")) {
            System.out.println("Ви хочете додати нову подію або видалити існуючу? (a/d):");
            action = scanner.nextLine();

            if (action.equalsIgnoreCase("a")) {
                System.out.println("Введіть назву події:");
                String eventName = scanner.nextLine();

                System.out.println("Введіть дату і час події (формат: yyyy-MM-dd'T'HH:mm:ss):");
                String eventDateTime = scanner.nextLine();

                Event event = new Event()
                        .setSummary(eventName)
                        .setStart(new EventDateTime().setDateTime(new DateTime(eventDateTime)))
                        .setEnd(new EventDateTime().setDateTime(new DateTime(eventDateTime)));

                // Додавання події
                event = service.events().insert(calendarId, event).execute();
                System.out.println("Подія додана: " + event.getHtmlLink());
            } else if (action.equalsIgnoreCase("d")) {
                System.out.println("Введіть дату події, яку ви хочете видалити (формат: yyyy-MM-dd):");
                String eventDate = scanner.nextLine();

                // Отримання списку подій за вказану дату
                DateTime startDateTime = new DateTime(eventDate + "T00:00:00");
                DateTime endDateTime = new DateTime(eventDate + "T23:59:59");
                Events events = service.events().list(calendarId)
                        .setTimeMin(startDateTime)
                        .setTimeMax(endDateTime)
                        .execute();

                List<Event> items = events.getItems();
                if (items.isEmpty()) {
                    System.out.println("Немає подій за вказану дату.");
                } else {
                    for (int i = 0; i < items.size(); i++) {
                        System.out.printf("%d. %s (%s)\n", i + 1, items.get(i).getSummary(), items.get(i).getId());
                    }

                    System.out.println("Введіть номер події, яку ви хочете видалити:");
                    int eventIndex = scanner.nextInt();
                    scanner.nextLine(); // Очищення буфера вводу

                    // Видалення події
                    service.events().delete(calendarId, items.get(eventIndex - 1).getId()).execute();
                    System.out.println("Подія видалена.");
                }
            }

            System.out.println("Введіть будь-яку клавішу, щоб продовжити, або 1, щоб вийти:");
            action = scanner.nextLine();
        }
    }
}
