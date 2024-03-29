//module com.example.rpgplatform {
//    requires javafx.controls;
//    requires javafx.fxml;
//
//    requires com.almasb.fxgl.all;
//
//    opens com.example.rpgplatform to javafx.fxml;
//
//    exports com.example.rpgplatform;
//    exports com.example.rpgplatform.MagicRoom;
//    exports com.example.rpgplatform.Components;
//    exports com.example.rpgplatform.Scenes;
//
//}
open module RPGPLATFORM.main{
    requires com.almasb.fxgl.all;
}