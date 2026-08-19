package br.com.fiap.reciconecta.navigation

sealed class Routes(val route: String) {


    object Splash: Routes("")

    object Onboarding: Routes("onboarding")

    object Home: Routes("")

    object CreatePassword: Routes("")

//Modelo aula ao vivo
   // object EditPassword: Routes("") {
    //    fun createRoute(passwordId: String): String {
   //         return "edit_password/$passwordId"
      //  }
 //   }

}