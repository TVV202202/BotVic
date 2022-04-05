import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;


public class Bot extends TelegramLongPollingBot {
    static MyCase myCase = new MyCase();

    public Bot() {
    }

    @Override
    public String getBotToken() {
        return "5113369285:AAHbuouPKxq3lEhjuRBMekbYxa_8Mirf-C4";
    }

    @Override
    public String getBotUsername() {
        return "BestVPhone_bot";
    }

    public static void main(String[] args) {
        //ApiContextInitializer.init();
        Bot bot = new Bot();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message=update.getMessage();
        if(update.hasMessage()){
            if(update.getMessage().hasText()){
                switch (message.getText()) {
                    case "Hello", "hello","Hi", "hi", "Привет", "привет", "хай!","/start", "start" -> {
                        // обнуление ранее запрошенных данных и ввод параметров заново
                        myCase.clear();
                        // функция вводов
                        sendMsg(message, "Привет! Я помогу подобрать тебе квартиру или комнату в Москве.");
                        sendMsg(message, "Давайте уточним параметры запроса. Ты готов? (да/нет)", "Да","Нет");
                    }
                    case "Да", "Нет, начнем заново" ->  sendMsg(message, "Что будем искать? (Квартира / Комната)"
                            , "Квартира", "Комната");
                    case "Квартира" -> {
                        myCase.setFlat(true);
                        sendMsg(message, "Сколько комнат в квартире, которую Вы ищете? ( 1, 2, 3, 4)"
                                , "1 комната", "2 комнаты", "3 комнаты", "4 комнаты");
                    }
                    case "1 комната","2 комнаты","3 комнаты","4 комнаты", "Комната" -> {
                        // надо запомнить выбор квартира или комната, если квартира, то кол-во комнат
                        if (message.getText().equals("Комната")){
                            myCase.setFlat(false);
                        }
                        else{
                            myCase.setRooms(message.getText().charAt(0)-'0');
                        }
                        sendMsg(message, "Теперь давайте определимся с типом дома. Выберите тип дома:"
                                , "Панельный","Блочный","Кирпичный","Монолит");
                    }
                    case "Панельный","Блочный","Кирпичный","Монолит" -> {
                        myCase.setTypeHouse(message.getText());
                        sendMsg(message, "Выберите предпочтительный этаж"
                                ,"1-й","2-й","3-й","4-й","5-й","6-й","7-й","8-й","9-й"
                                ,"10-й","11-й","12-й");
                    }

                    case "1-й","2-й","3-й","4-й","5-й","6-й","7-й","8-й","9-й","10-й","11-й","12-й" -> {
                        // надо запомнить выбор
                        int floor = Integer.parseInt(message.getText().replaceAll("[^\\d]", ""));
                        myCase.setFloor(floor);
                        if (myCase.getFloor() <= 5) {
                            sendMsg(message, "Наличие лифта", "Есть лифт", "Нет лифта");
                        }
                        else{ // в домах выше 5 этажей всегда есть лифт
                            myCase.setElevator(true);
                            sendMsg(message, "Давайте проверим, правильно ли я все понял:", "Проверка введенной информации");
                        }
                    }
                    case "Есть лифт" -> {
                        myCase.setElevator(true);
                        sendMsg(message, "Давайте проверим, правильно ли я все понял:", "Проверка введенной информации");
                    }
                    case "Нет лифта" -> {
                        myCase.setElevator(false);
                        sendMsg(message, "Давайте проверим, правильно ли я все понял:", "Проверка введенной информации");
                    }
                    case "Проверка введенной информации" -> {
                        if (myCase.isFlat()){
                            sendMsg(message, "Ищем: Квартиру");
                        }
                        else {
                            sendMsg(message, "Ищем: Комнату");
                        }
                        sendMsg(message, "Дом: " + myCase.getTypeHouse());
                        sendMsg(message, "Этаж: " + myCase.getFloor());
                        if (myCase.isFlat()){
                            sendMsg(message, "Количество комнат: " + myCase.getRooms());
                        }
                        if (myCase.isElevator()){
                            sendMsg(message, "Лифт есть");
                        }
                        else {
                            sendMsg(message, "Лифта нет");
                        }
                        if (myCase.getKitchen() != null){
                            sendMsg(message, "Кухня: " + myCase.getKitchen());
                        }
                        sendMsg(message, "Всё правильно?", "Ok", "Нет, начнем заново");
                    }
                    case "Ok" ->{
                        sendMsg(message, "Спасибо за информацию. Пойду искать... Если найду где :)");
                        sendMsg(message, "Может сузим диапазон поиска?", "Давайте сузим","Нет, давай все варианты");
                    }
                    case "Нет, давай все варианты" ->{
                        sendMsg(message, "Ловите: список вариантов (partition under reconstruction...)");
                        sendMsg(message, "Начать новый поиск? (да/нет)", "Да","Нет");
                    }
                    case "Давайте сузим" ->{
                        sendMsg(message, "Выберите метраж кухни:", "5.5", "6", "8", "10", "12", "15", "больше 15");
                    }
                    case "5.5", "6", "8", "10", "12", "15", "больше 15" ->{
                        myCase.setKitchen(message.getText());
                        sendMsg(message, "Давайте проверим, правильно ли я все понял:", "Проверка введенной информации");
                    }
                    case "Нет" -> {
                        sendMsg(message, "Пока-пока! Заходите когда будете готовы! Буду ждать...");
                        //sendMsg(message, "Для старта наберите: старт");
                    }
                    case "дурак", "тупой", "глупый", "идиот" -> {
                        sendMsg(message, "Не стреляйте в пианиста, он играет как умеет!");
                    }
                    default -> {
                        sendMsg(message, "Извините, я не понимаю... :( Для начала работы поздоровайтесь или наберите start");
                    }
                }
            }
        }else if(update.hasCallbackQuery()){ // была задумка написать и обрабатывать CallBack,
            // но не смог обработать ответ, потому фактически заглушка
            sendCallback(update.getCallbackQuery());
        }
    }

    private void sendMsg(Message message, String text, String ... buttonText) {
        // выводит сообщение и новую клавиатуру (количество и текст кнопок произвольны, но не больше одной строки)
        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId().toString()).text(text)
                .build();
        if (buttonText.length != 0){
            setButtons(sendMessage, buttonText);
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void setButtons(SendMessage sendMessage, String ... textAr) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard  = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        for (String el: textAr){
            keyboardFirstRow.add(new KeyboardButton(el));
        }
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private void sendCallback(CallbackQuery callbackQuery) {
        SendMessage sendMessage;
        sendMessage = SendMessage.builder().text(callbackQuery.getData())
                .chatId(callbackQuery.getMessage().getChatId().toString()).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

class MyCase {
    private boolean flat; // квартира - True, комната - False
    private int rooms; // 1-4
    private String typeHouse; // "Панельный", "Блочный", "Кирпичный", "Монолит"
    private boolean elevator; // есть, нет
    private int floor; // 1-12
    private String kitchen;

    public String getKitchen() { return kitchen; }

    public void setKitchen(String kitchen) { this.kitchen = kitchen; }

    public boolean isFlat() {
        return flat;
    }

    public void setFlat(boolean flat) {
        this.flat = flat;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public String getTypeHouse() {
        return typeHouse;
    }

    public void setTypeHouse(String typeHouse) {
        this.typeHouse = typeHouse;
    }

    public boolean isElevator() {
        return elevator;
    }

    public void setElevator(boolean elevator) {
        this.elevator = elevator;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void clear() {
        flat=false; // квартира - True, комната - False
        rooms=0; // 1-4
        typeHouse=""; // 1-"Панельный", 2-"Блочный", 3-"Кирпичный", 4-"Монолит"
        elevator=false; // есть, нет
        floor=0;
        kitchen = null;
    }
}

/*
    public static SendMessage sendInlineKeyBoardMessage(String chatId, String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Раз кнопка");
        //inlineKeyboardButton1.setCallbackData("Button \"Раз\" has been pressed");
        String s = inlineKeyboardButton1.getText();
        inlineKeyboardButton1.setCallbackData(s);

        inlineKeyboardButton2.setText("Три кнопка");
        inlineKeyboardButton2.setCallbackData("Button \"Три\" has been pressed");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(InlineKeyboardButton.builder().text("Два кнопка").callbackData("Button \"Два\" has been pressed").build());
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);

        return SendMessage.builder().chatId(chatId).text(text).replyMarkup(inlineKeyboardMarkup).build();
    }
 */
    /*
    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        //sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }*/