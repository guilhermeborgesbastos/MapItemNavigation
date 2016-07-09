package guilherme.com.br.mapitemnavigation;


/*
 * Created by Guilherme Borges Bastos on 17/07/15.
 */


public class Application extends android.app.Application {

    public static String storagePath = "https://meucomercioeletronico.com/tutorial/images";

    //set Language  / Aplica a língua
    public static String language = "pt_br";
    //public static String language = "en";

    // lista de categorias das lojas
    // category store list
    public static String category1;
    public static String category2;
    public static String category3;

    public static String unity;
    public static String downTown;

    // lista de lojas
    // store list
    public static String store1;
    public static String store1LogoPath;
    public static String store2;
    public static String store2LogoPath;
    public static String store3;
    public static String store3LogoPath;


    @Override
    public void onCreate() {
        super.onCreate();
    }


    public Application() {

        if(language.equals("pt_br")){
            //portuguease
            unity = "Unidade";
            downTown = "Centro";

            category1 = "Vestuário";
            category2 = "Alimentação";
            category3 = "Lazer";

            store1 = "Casas Bahia";
            store1LogoPath =  storagePath + "/casas-bahia.jpg";
            store2 = "Starbucks";
            store2LogoPath = storagePath + "/starbucks.png";
            store3 = "C&A";
            store3LogoPath = storagePath + "/cea.jpg";

        } else {
            //English
            unity = "Store Branch";
            downTown = "Downtown";

            category1 = "Clothing";
            category2 = "Feeding";
            category3 = "Recreation";

            store1 = "Nike";
            store1LogoPath = storagePath + "/nike.jpg";
            store2 = "Adidas";
            store2LogoPath = storagePath + "/adidas.jpg";
            store3 = "Ralph Lauren";
            store3LogoPath = storagePath + "/ralph-lauren.jpg";
        }

    }

}