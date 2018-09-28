import javafx.scene.Parent;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.logging.Log;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Example extends TelegramLongPollingBot {
    private static Vector<Long> ID1, ID2;//Массивы для хранения ID пользователей
    private static Vector<String> ID1N, ID2N;//Массивы для хранения информации о пользователе
    private static Vector<String> ID1F, ID2F;//Массивы для хранения информации о Фирме
    private static Vector<String> SerInfo;//Массив с данными о качествеобслуживания
    private static Logger log = Logger.getLogger(Example.class.getName());//Логер для вывода инфромации
    private static Long Master = 242948286L;//ID пользователя с доступом к отладке
    private static Vector<Long> ToAut = new Vector<>();
    private static Vector<Integer> ToAutN = new Vector<>();
    private static Vector<String> ToAutК = new Vector<>();
    private static Vector<String> ToAutF = new Vector<>();
    private static Example This;

    public static void main(String[] args) {
        Vector<Vector> ByferMassV;
        Vector<String> ByferMassF;
        String Patch = "ID1.txt";
        String Patch2 = "ID2.txt";
        ByferMassV = getID("ID1.txt");//Чтение данных о пользователях
        if (ByferMassV != null) {
            ID1 = ByferMassV.get(0);
            ID1N = ByferMassV.get(1);
        }
        ByferMassV = getID("ID2.txt");
        if (ByferMassV != null) {
            ID2 = ByferMassV.get(0);
            ID2N = ByferMassV.get(1);
        }
        ByferMassF = getIDF("ID1F.txt");
        if (ByferMassF != null) ID1F = ByferMassF;
        ByferMassF = getIDF("ID2F.txt");
        if (ByferMassF != null) ID2F = ByferMassF;
        ByferMassF = getIDF("SerInfo.txt");
        if (ByferMassF != null) SerInfo = ByferMassF;
        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            This=new Example();
            new Thread(() -> {{
                try {
                   while (true){
                       Thread.sleep(5000);
                       GetOnlainInf();
                }
                } catch (InterruptedException qq) {
                    qq.printStackTrace();
                }
                }
            }).start();
            botapi.registerBot(This);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "itsh_bot";
    }

    @Override
    public void onUpdateReceived(Update e) {
        Message msg = e.getMessage();
        String txt = msg.getText();
        Long Role = msg.getChatId();
        log.info("Пришло сообщение пользователя!");
        if (Role.equals(Master)) {
            log.info("Это мастер!");
            ToMasterchat(txt);
            return;
        }

        for (int g = 0; g < ID1.size(); g++) {
            if (ID1.get(g).equals(Role)) {
                log.info("Это из Каршеринга!");
                ToID1chat(txt, Role);
                return;
            }
        }

        for (int g = 0; g < ID2.size(); g++) {
            if (ID2.get(g).equals(Role)) {
                log.info("Это из Сервиса!");
                ToID2chat(txt, Role);
                return;
            }
        }

        if (ToAut.size() != 0)
            for (int g = 0; g < ToAut.size(); g++) {
                log.info("Это тот пользователь только проходит регистрацию!");
                GetMeID(msg, true, g);
                return;
            }
        log.info("Это тот пользователь не найден!");
        if (txt.equals("/GetMeID")) {
            GetMeID(msg, false, ToAut.size() + 1);
            return;
        }

        sendMsg(msg, "Ваши данные не найдены в базе, зарегестрируйтесь командой /GetMeID");
    }

    @Override
    public String getBotToken() {
        return "655049533:AAHKXP--e7LOpKe8dghUwnZcYX4rxLhn71M";
    }

    private void HelloAll() {
        SendMessage s = new SendMessage();
        String text = "Здраствуте, вы подкючились к MOBILCAR SERVICE, это сообщение проверка работоспособности сервиса ";
        s.setChatId(Master);
        s.setText(text + " Мастер ");
        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        for (int g = 0; g < ID1.size(); g++) {
            s.setChatId(ID1.get(g));
            s.setText(text + " уважаемый " + ID1N.get(g) + " из " + ID1F.get(g) + " приветсвуем вас в нашем чат-боте в Телеграме ");
            log.info("Отправка пользователю" + ID1N.get(g) + " c ID = " + ID1.get(g) + " сообщения с приветсвием");
            try {
                execute(s);
            } catch (TelegramApiException e) {
                log.info("Отправка не удалась");
                log.info(e.getMessage());
            }
        }

        for (int g = 0; g < ID2.size(); g++) {
            s.setChatId(ID2.get(g));
            s.setText(text + " уважаемый " + ID2N.get(g) + " из " + ID2F.get(g) + " приветсвуем вас в нашем чат-боте в Телеграмме ");
            log.info("Отправка пользователю" + ID2N.get(g) + " c ID = " + ID2.get(g) + " сообщения с приветсвием");
            try {
                execute(s);
            } catch (TelegramApiException e) {
                log.info("Отправка не удалась");
                log.info(e.getMessage());
            }
        }
    }

    private static void notWorker(String CAR ) {

        SendMessage s = new SendMessage();
        String text = "";
        for (int g = 0; g < ID1.size(); g++) {
            s.setChatId(ID1.get(g));
            s.setText(text + "Уважаемый " + ID1N.get(g) + ", в данный момент ваша заявка по обслуживанию автомобиля id="+CAR+" будет обработана дольше, чем обычно ");
            log.info("Отправка пользователю" + ID1N.get(g) + " c ID = " + ID1.get(g) + " сообщения с задержкой обслужиания");
            try {
                This.execute(s);
            } catch (TelegramApiException e) {
                log.info("Отправка не удалась");
                log.info(e.getMessage());
            }
        }
    }
    private static void WaTnotWorker(Long ID, String CAR, String Type ) {

        SendMessage s = new SendMessage();
        int n=0,b=0;
        for(int g=0;g<ArrayTOapi.address.size();g++)
            if(ArrayTOapi.id.get(g)==CAR) {
            n=g;
            break;
        }
        for(int g=0;g<ID2.size();g++)
            if(ID2.get(g).equals(ID)) {
                b=g;
                break;
            }
        s.setChatId(ID);
        String text = "Здравствуйте, пожалуйста примите заявку на обслуживание автомобиля, от каршеринга "+ID1F.get(0)+" его ID="+CAR+", по адресу:"+ArrayTOapi.address.get(n)+", цвет автомобиля - "+ArrayTOapi.color.get(n)+", марка - "+ArrayTOapi.mark.get(n)+", модель - "+ArrayTOapi.model.get(n)+", тип работы - "+Type+" (1 - заправка, 2 - проверка автомобиля, 3 - зарядка АКБ) введите /StartWork для начала приема заявки" ;
        s.setText(text);
        try {
                log.info("Отправка пользователю" + ID2N.get(b) + " c ID = " + ID + " сообщения с просьбой об обслуживании");
                This.execute(s);
            } catch (TelegramApiException e) {
                log.info("Отправка не удалась");
                log.info(e.getMessage());
            }
    }

    private void ToID1chat(String Mseg, long ID) {
    if(Mseg.equals("/Work")){
        StringBuilder Text=new StringBuilder("В данный момент ведутся работы над:\n");
        for(int g=0;g<ArrayTOapi.idWorker.size();g++)
            Text.append("ID работника - "+ArrayTOapi.idWorker.get(g)+"\nID автомобиля - "+ArrayTOapi.idCar.get(g)+"\nРаботник ответил:"+ArrayTOapi.idWorkerSay.get(g));
        sendMsgID(ID,Text.toString());
    }
    }

    private void ToID2chat(String Mseg, long ID) {
        if(Mseg.equals("/StartWork")){
            int set=0;
            StringBuilder Text=new StringBuilder("Заявка успешно принята. По окончании работы сообшите об этом с помощью команды /StopWork\n");
            { for(int g=0;g<ArrayTOapi.idWorker.size();g++)
                if(Long.parseLong(ArrayTOapi.idWorker.get(g))==ID) {
                    ArrayTOapi.idWorkerSay.set(g, "Yes");
                    set=g;
                }
            }
            int k=0;
            for(k=0;k<ID2.size();k++)
                if(ID2.get(k)== ID) break;
            sendMsgID(ID,Text.toString());
            Text=new StringBuilder("Ваша заявка на обслуживание автомобиля "+ArrayTOapi.mark.get(set)+"  "+ArrayTOapi.model.get(set)+" ID="+ArrayTOapi.idCar.get(set)+" принята. Сервис - "+ID2F.get(k));
            sendMsgID(ID1.get(0),Text.toString());
        }
        if(Mseg.equals("/StopWork")){
            int set=0;
            StringBuilder Text=new StringBuilder("Работа завершена, данные будут отправлены каршеринговой фирме.\n");
            for(set=0;set<ArrayTOapi.idWorker.size();set++)
                if(Long.parseLong(ArrayTOapi.idWorker.get(set))==ID) break;

            int k;
            for(k=0;k<ID2.size();k++)
                if(ID2.get(k)== ID) break;
            sendMsgID(ID,Text.toString());
            Text=new StringBuilder("Работа по обслуживанию автомобиля "+ArrayTOapi.mark.get(set)+" "+ArrayTOapi.model.get(set)+" ID="+ArrayTOapi.idCar.get(set)+" успешно завершена. Сервис - "+ID2F.get(k) + ". Чек на оказание выполненн  ых услуг доступен по ссылке: https://www.nalog.ru/rn33/news/activities_fts/6545208/");
            sendMsgID(ID1.get(0),Text.toString());
            for(set=0;set<ArrayTOapi.idWorker.size();set++)
                if(Long.parseLong(ArrayTOapi.idWorker.get(set))==ID){
                    ArrayTOapi.idWorker.remove(set);
                    ArrayTOapi.idWorkerSay.remove(set);
                    ArrayTOapi.idCar.remove(set);
                    ArrayTOapi.idType.remove(set);
                }
        }
    }

    private void ToMasterchat(String Mseg) {
        switch (Mseg) {
            case "/AllMeseg": {
                HelloAll();
            }
            case "/Test":{
                GetOnlainInf();
            }
        }
    }

    private void GetMeID(Message Mseg, boolean type, int num) {
        if (type) {
            String Text = Mseg.getText();
            if (ToAutN.get(num).equals(-1) && (Text.equals("Каршеринг") || Text.equals("Сервис"))) {
                if (Text.equals("Каршеринг")) ToAutN.set(num, 1);
                else ToAutN.set(num, 2);
                sendMsg(Mseg, "Введите  Ф.И.О");
                return;
            } else if (ToAutК.get(num).equals("")) {
                ToAutК.set(num, Text);
                sendMsg(Mseg, "Введите название вашей организации");
                return;
            } else if (ToAutК.get(num).equals("")) {
                ToAutК.set(num, Text);
                sendMsg(Mseg, "Введите название вашей организации");
                return;
            } else if (ToAutF.get(num).equals("")) {
                ToAutF.set(num, Text);
                sendMsg(Mseg, "Проверьте правильность данных и введите Да/Нет \nФ.И.О " + ToAutК.get(num) + "\nИз организации " + ToAutF.get(num) + "\nРоль - " + ToAutN.get(num) + " (1 - Работник каршеринга, 2 - Работник сервиса)");
                return;
            } else if (Text.equals("Да")) {
                sendMsg(Mseg, "Регистрация успешно пройдена");

                WritToBase(num);
                return;
            }
        } else {
            ToAut.add(Mseg.getChatId());
            ToAutN.add(-1);
            ToAutК.add("");
            ToAutF.add("");
            sendMsg(Mseg, "Введите к какому типу организации вы относитесь:Каршеринг или Сервис");
            return;
        }
    }

    @SuppressWarnings("deprecation")
    private void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText(text);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendMsgID(Long id, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(id);
        s.setText(text);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static Vector<Vector> getID(String Path) {
        try {
            File file = new File(Path);
            Scanner scan = new Scanner(file);
            Vector<Long> ID = new Vector<>();
            Vector<String> IDN = new Vector<>();
            while (scan.hasNextLong()) {
                ID.add(scan.nextLong());
                IDN.add(scan.nextLine());
            }
            Vector<Vector> My = new Vector<>();
            My.add(ID);
            My.add(IDN);
            return My;
        } catch (Exception M2) {
            M2.getMessage();
            log.info("Ошибка при чтении данных с базы! Нельзя будет определить роль пользователя!");
            Vector<Vector> My = new Vector<>();
            My.add(new Vector<>());
            My.add(new Vector<>());
            return My;
        }
    }

    private static void WritToBase(int num) {
        String Patch = "", Patch2 = "";
        if (ToAutN.get(num).equals(1)) {
            Patch = "ID1.txt";
            Patch2 = "ID1F.txt";
        } else {
            Patch = "ID2.txt";
            Patch2 = "ID2F.txt";
        }
        try {
            FileWriter writer = new FileWriter(Patch, true);
            FileWriter writer2 = new FileWriter(Patch2, true);
            // запись всей строки
            writer.append('\n');
            writer2.append('\n');
            writer.write(ToAut.get(num) + " " + ToAutК.get(num));
            writer2.write(ToAutF.get(num));
            writer.flush();
            writer2.flush();
            if (ToAutN.get(num).equals(1)) {
                ID1.add(ToAut.get(num));
                ID1N.add(ToAutК.get(num));
                ID1F.add(ToAutF.get(num));
            } else {
                ID2.add(ToAut.get(num));
                ID2N.add(ToAutК.get(num));
                ID2F.add(ToAutF.get(num));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    private static Vector<String> getIDF(String Path) {
        try {
            File file = new File(Path);
            Scanner scan = new Scanner(file);
            Vector<String> IDN = new Vector<>();
            while (scan.hasNext()) {
                IDN.add(scan.nextLine());
            }
            return IDN;
        } catch (Exception M2) {
            M2.getMessage();
            log.info("Ошибка при чтении данных с базы! Нельзя будет определить роль пользователя!");
            return null;
        }
    }

    private static void GetOnlainInf() {
        try {
            InputStream is = new URL("https://cs.mongeo.ru/api/car/list").openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                ArrayTOapi.StringToMass(jsonText);
            } finally {
                is.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ArrayTOapi.StringToMass("");
        }


    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static class ArrayTOapi {
        static ArrayList<String> id = new ArrayList<>(15);
        static ArrayList<String> lat = new ArrayList<>(15);
        static ArrayList<String> lon = new ArrayList<>(15);
        static ArrayList<String> angle = new ArrayList<>(15);
        static ArrayList<String> transmission = new ArrayList<>(15);
        static ArrayList<String> parkingPermit = new ArrayList<>(15);
        static ArrayList<String> color = new ArrayList<>(15);
        static ArrayList<String> maxFuelLevel = new ArrayList<>(15);
        static ArrayList<String> discount = new ArrayList<>(15);
        static ArrayList<String> multiplier = new ArrayList<>(15);
        static ArrayList<String> model = new ArrayList<>(15);
        static ArrayList<String> mark = new ArrayList<>(15);
        static ArrayList<String> fuel = new ArrayList<>(15);
        static ArrayList<String> fuelDistance = new ArrayList<>(15);
        static ArrayList<String> address = new ArrayList<>(15);

        protected static void StringToMass(String Text) {
            Text = Text.replaceAll("[\\[\\]{}\"]", "");
            Text = Text.replaceAll("[:,]", " ");
            String Mass[] = Text.split(" ");
            id.clear();
            lat.clear();
            lon.clear();
            angle.clear();
            transmission.clear();
            parkingPermit.clear();
            color.clear();
            maxFuelLevel.clear();
            discount.clear();
            multiplier.clear();
            model.clear();
            mark.clear();
            fuel.clear();
            Pattern idPattern = Pattern.compile("id [a-z|A-Z|0-9|,|.]+ lat");
            Matcher find = idPattern.matcher(Text);
            while (find.find())
                id.add(find.group().split(" ")[1]);

            idPattern = Pattern.compile("lat [a-z|A-Z|0-9|,|.]+ lon");
            find = idPattern.matcher(Text);
            while (find.find())
                lat.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("lon [a-z|A-Z|0-9|,|.]+ angle");
            find = idPattern.matcher(Text);
            while (find.find())
                lon.add(find.group().split(" ")[1]);

            idPattern = Pattern.compile("angle [a-z|A-Z|0-9|,|.]+ transmission");
            find = idPattern.matcher(Text);
            while (find.find())
                angle.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("transmission [a-z|A-Z|0-9|,|.]+ parkingPermit");
            find = idPattern.matcher(Text);
            while (find.find())
                transmission.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("parkingPermit [a-z|A-Z|0-9|,|.]+ color");
            find = idPattern.matcher(Text);
            while (find.find())
                parkingPermit.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("color [а-я|А-Я|0a-z|A-Z|0-9|,|.]+ fuel");
            find = idPattern.matcher(Text);
            while (find.find())
                color.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("fuel [a-z|A-Z|0-9|,|.]+ maxFuelLevel");
            find = idPattern.matcher(Text);
            while (find.find())
                fuel.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("maxFuelLevel [a-z|A-Z|0-9|,|.]+ discount");
            find = idPattern.matcher(Text);
            while (find.find())
                maxFuelLevel.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("fuel [a-z|A-Z|0-9|,|.]+ discount");
            find = idPattern.matcher(Text);
            while (find.find())
                fuel.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("discount [a-z|A-Z|0-9|,|.]+ multiplier");
            find = idPattern.matcher(Text);
            while (find.find())
                discount.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("multiplier [a-z|A-Z|0-9|,|.]+ model");
            find = idPattern.matcher(Text);
            while (find.find())
                multiplier.add(find.group().split(" ")[1]);


            idPattern = Pattern.compile("model .+ mark");
            find = idPattern.matcher(Text);
            while (find.find()) {
                String Model[] = find.group().split(" ");
                StringBuilder ModelT = new StringBuilder("");
                boolean log = false;
                for (int g = 0; g < Model.length; g++) {
                    if (Model[g].equals("model")) {
                        log = true;
                        continue;
                    }
                    if (Model[g].equals("mark")) {
                        if (ModelT.length() != 0) model.add(ModelT.toString());
                        ModelT = new StringBuilder("");
                        log = false;
                        continue;
                    }
                    if (log) {
                        ModelT.append(" ");
                        ModelT.append(Model[g]);
                    }
                }

            }

            idPattern = Pattern.compile("(mark [a-z|A-Z|0-9|,|.| ]+ address)");
            find = idPattern.matcher(Text);
            while (find.find()) {
                String Mark[] = find.group().split(" ");
                StringBuilder MarkL = new StringBuilder("");
                for (int g = 1; g < Mark.length - 1; g++) {
                    if (Mark[g].equals("fuelDistance") || Mark[g].equals("address")) break;
                    MarkL.append(Mark[g]);
                    MarkL.append(" ");
                }
                mark.add(MarkL.toString());
            }


            idPattern = Pattern.compile("fuelDistance [а-я|А-Я|a-z|A-Z|0-9|,|.]+ address");
            find = idPattern.matcher(Text);
            while (find.find())
                fuelDistance.add(find.group().split(" ")[1]);
            for (int g = fuelDistance.size(); g < id.size(); g++)
                fuelDistance.add("No");
            String AdreesM[] = Text.split(" ");
            StringBuilder AdressB = new StringBuilder();
            boolean log = false;
            for (int g = 0; g < AdreesM.length; g++) {
                if (AdreesM[g].equals("address")) {
                    log = true;
                    continue;
                }
                if (AdreesM[g].equals("id")) {
                    if (AdressB.length() != 0) address.add(AdressB.toString());
                    AdressB = new StringBuilder("");
                    log = false;
                    continue;
                }
                if (log) {
                    AdressB.append(" ");
                    if (AdreesM[g - 1].equals("улица")) AdressB.append(", ");
                    AdressB.append(AdreesM[g]);
                }
            }
            address.add(AdressB.toString());
            getWork();
            return;
        }

        static ArrayList<String> idWorker = new ArrayList<>(0);
        static ArrayList<String> idWorkerSay = new ArrayList<>(0);
        static ArrayList<String> idType = new ArrayList<>(0);
        static ArrayList<String> idCar = new ArrayList<>(0);
        static int max[];

        private static void getWork() {
            max = new int[SerInfo.size()];
            for (int g = 0; g < SerInfo.size(); g++) {
                max[g] = Integer.parseInt(SerInfo.get(g));
            }
            int temp;
            for (int i = 0; i < SerInfo.size() - 1; i++) {
                for (int j = 0; j < SerInfo.size() - i - 1; j++) {
                    if (max[j] > max[j + 1]) {
                        temp = max[j];
                        max[j] = max[j + 1];
                        max[j + 1] = temp;
                    }
                }
            }
            for (int g = 0; g < id.size(); g++) {
                if (Integer.parseInt(fuel.get(g)) < 10) {
                    Tofuel(id.get(g), "1");
                }
                if (Integer.parseInt(discount.get(g)) > 15) {
                    Tofuel(id.get(g), "2");
                }

                if (Double.parseDouble(lon.get(g)) > 25) {
                    Tofuel(id.get(g), "3");
                }
            }
        }

        private static void Tofuel(String IDcar, String Type) {
            int maxl = 0;
            for (maxl = 0; maxl < idWorker.size(); maxl++) {
                for (int g = 0; g < SerInfo.size(); g++) {
                    if (max[g] != Integer.parseInt(idWorker.get(maxl))) ;
                    break;
                }
            }
            if (maxl == ID2N.size()) {
                notWorker(IDcar);
                return;
            }

            idWorker.add(ID2.get(maxl).toString());
            idWorkerSay.add("No");
            idType.add(Type);
            idCar.add(IDcar);
            WaTnotWorker(ID2.get(maxl),IDcar,Type);

        }
    }

}
