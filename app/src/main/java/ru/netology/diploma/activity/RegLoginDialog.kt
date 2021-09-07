package ru.netology.diploma.activity

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import ru.netology.diploma.R


enum class Dialog {
    LOGIN, REGISTER, REGISTER_AVATAR
}

 fun Activity.showLoginAuthDialog(dialogType : Dialog, action: (login :String, password: String, name : String) -> Unit ) {
     val dialogBuilder = AlertDialog.Builder(this)
     val dialogView = layoutInflater.inflate(R.layout.login_auth, null)

     dialogBuilder.setView(dialogView)
         .setPositiveButton("OK") { a, b ->
             val login = dialogView.findViewById<EditText>(R.id.login)
             val password = dialogView.findViewById<EditText>(R.id.password)
             val name = dialogView.findViewById<EditText>(R.id.name)

             action(login.text.toString(), password.text.toString(), name.text.toString())
         }
         .setNegativeButton("cancel") { a, b ->

         }

     when (dialogType) {
         Dialog.LOGIN -> {
             dialogBuilder.setTitle(R.string.sign_in)
         }

         Dialog.REGISTER -> {
             dialogBuilder.setTitle(R.string.sign_up)

         }

         Dialog.REGISTER_AVATAR -> {
             dialogBuilder.setTitle(R.string.sign_up)
         }
     }

     val alertDialog = dialogBuilder.create()

     when (dialogType) {
         Dialog.REGISTER -> {
             showRegFields(dialogView, alertDialog)
         }

         Dialog.REGISTER_AVATAR -> {
             showRegFields(dialogView, alertDialog)
         }
     }

     alertDialog.show()

     when (dialogType) {
         Dialog.REGISTER,  Dialog.REGISTER_AVATAR -> {
             alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                 false
         }
     }


 }

private fun showRegFields(
    dialogView: View,
    alertDialog: AlertDialog
) {

    val password = dialogView.findViewById<EditText>(R.id.password)

    val passwordL = dialogView.findViewById<TextView>(R.id.passwordL2)
    val password2 = dialogView.findViewById<EditText>(R.id.password2)
    val nameL = dialogView.findViewById<TextView>(R.id.nameL)
    val name = dialogView.findViewById<EditText>(R.id.name)

    passwordL.visibility = View.VISIBLE
    password2.visibility = View.VISIBLE
    name.visibility = View.VISIBLE
    nameL.visibility = View.VISIBLE



    password.addTextChangedListener {
        checkField(password, password2, alertDialog)
    }

    password2.addTextChangedListener {
        checkField(password2, password, alertDialog)
    }
}

private fun checkField(
    password: EditText,
    password2: EditText,
    alertDialog: AlertDialog
) {
    if (password.text.toString() == password2.text.toString()) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
            true

    } else {
        password.error = "Passwords don't matching"
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
            false
    }
}

fun Activity.showFailDialog() {
    runOnUiThread{
    AlertDialog.Builder(this)
        .setTitle("Неудача")
        .setMessage("Вход не удался :( ")
        .setPositiveButton(
            android.R.string.ok
        ) { dialog, which ->
        }
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show()
 }
}