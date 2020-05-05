package mx.edu.ittepic.ladm_u3_pr2_castillofranquez

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    var id = ""
    var basedatos = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var extras = intent.extras

        id = extras!!.getString("id").toString()
        editText.setText(extras.getString("nombre"))
        editText2.setText(extras.getString("domicilio"))
        editText3.setText(extras.getString("telefono"))
        editText4.setText(extras.getString("producto"))
        editText5.setText(extras.getDouble("precio").toString())
        editText6.setText(extras.getDouble("cantidad").toString())
        if(extras.getBoolean("estado")==true){
            checkBox.isChecked= true
        }else{
            checkBox.isChecked= false
        }


        button.setOnClickListener {
            basedatos.collection("restaurante")
                .document(id)
                .update("nombre",editText.text.toString(),
                    "domicilio",editText2.text.toString(),
                    "telefono",editText3.text.toString(),
                    "producto.producto",editText4.text.toString(),
                    "producto.precio",editText5.text.toString().toDouble(),
                    "producto.cantidad",editText6.text.toString().toDouble(),
                    "producto.entregado",checkBox.isChecked)
                .addOnSuccessListener {
                    Toast.makeText(this,"ACTUALIZACION REALIZADA", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"ERROR! NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG)
                        .show()
                }
        }
        button2.setOnClickListener {
            finish()
        }
    }
}
