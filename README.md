# Map Item Navigation
Recentemente em um dos projetos precisei criar um mapa dinâmico, onde deveria ser exibido uma lista com as filiais de lojas que estivessem até 1Km da localização do usuário. Tirei o dia hoje para mover todo o código desenvolvido para um projeto separado, irei compartilhá-lo com você neste artigo. 

No projeto original o POJO ( Filial & Loja ) são populados pela API, para poder compartilhar o código,  criei um  List<Filial> com 3 lojas e 5 filiais.

Baixe agora o código fonte!

![ContactPikerAnimated](https://meucomercioeletronico.com/tutorial/MapItemNavigation.gif) 


## Instalação e uso
Basta importar o projeto do Git para o seu editor favorito e efetuar a troca do API_KEY.

Para isto edite o arquivo AndroidManifest.xml linha 26.
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="guilherme.com.br.mapitemnavigation">


    <!-- ========================= Global Permissions ====================================== -->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />


    <application
        android:name=".Application"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <!-- ========================= Google Maps Specific ===================================== -->
        <!--Change Here Your APi Key em: -->
        <!-- Mude aqui sua APi Key saiba mais em: -->
        <!-- https://developers.google.com/maps/documentation/android-api/signup -->
        <meta-data
            android:name="com.google.android.maps.v2.API_KEY"
            android:value="YOUR_API_KEY_HERE" />

        <activity
            android:name=".MainActivity"
            android:label="@string/app_name"
            android:theme="@style/AppTheme.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- ========================= ExpandedMapActivity Activities ===================================== -->

        <activity
            android:name=".ExpandedMapActivity"
            android:label="ExpandedMapActivity"
            android:screenOrientation="portrait">
        </activity>


    </application>

</manifest>
```

Caso não tenha a API KEY, este link irá ajudá-lo:
https://developers.google.com/maps/documentation/android-api/signup

## Agradecimentos
Espero que tenha ajudado!

Fico a disposição para tirar dúvidas: 
guilhermeborgesbastos@gmail.com

## Contato
[![VIDEO](https://media.licdn.com/mpr/mpr/shrinknp_100_100/AAEAAQAAAAAAAAgiAAAAJGMwMTQwNTMyLTU2N2EtNDM1NS1iZDMxLTI2ZjVhZDRlNjM2Mw.jpg)](https://www.facebook.com/AndroidNaPratica)
