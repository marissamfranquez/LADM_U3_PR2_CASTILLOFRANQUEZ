package mx.edu.ittepic.ladm_u3_pr2_castillofranquez

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var dataLista = ArrayList<String>()
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        insertar.setOnClickListener {
            insertarRegistro()
        }

        button4.setOnClickListener {

            baseRemota.collection("restaurante")
                .whereEqualTo("nombre",buscar.text.toString())
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    if(firebaseFirestoreException!=null){
                        resultado.setText("NO SE PUDO REALIZAR BUSQUEDA")
                        return@addSnapshotListener
                    }

                    var res = ""
                    for(document in querySnapshot!!){
                        res+= "Nombre: "+document.getString("nombre")+"\n"+"Domicilio: "+document.getString("domicilio")+ "\n"+
                                "Celular: "+document.getString("telefono")+"\n"+"Producto: "+document.getString("producto.producto")+"\n"+
                                "Precio: "+ document.getDouble("producto.precio") + "\n"+ "Cantidad: " + document.getDouble("producto.cantidad")+ "\n"+
                                "Estado: "+document.getBoolean("producto.entregado")
                    }
                    resultado.setText(res)
                    nombre.setText("");
                    domicilio.setText("");
                    celular.setText("");
                    producto.setText("");
                    precio.setText("");
                    cantidad.setText("");
                }
        }


        baseRemota.collection("restaurante")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    Toast.makeText(this,"ERROR NO SE PUEDE ACCEDER A CONSULTA", Toast.LENGTH_LONG)
                        .show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()
                for (document in querySnapshot!!){
                    var cadena = "Nombre: "+document.getString("nombre")+" | "+"Domicilio: "+document.getString("domicilio")+ " | "+
                            "Celular: "+document.getString("telefono")+" | "+"Producto: "+document.getString("producto.producto")+" | "+
                            "Precio: "+ document.getDouble("producto.precio") + " | "+ "Cantidad: " + document.getDouble("producto.cantidad")+ " | "+
                            "Estado: "+document.getBoolean("producto.entregado")

                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
                if(dataLista.size==0){
                    dataLista.add("NO HAY DATA")
                }
                var adaptador = ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,dataLista)
                lista.adapter = adaptador
            }



        lista.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener
            }
            AlertEliminarActualizar(position)
        }
    }
    private fun AlertEliminarActualizar(position: Int) {
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage("¿Que deseas hacer con \n${dataLista[position]}?")
            .setPositiveButton("Eliminar") { d, w ->

                eliminar(listaID[position])

            }
            .setNegativeButton("Actualizar") { d, w ->
                llamarVentanaActualizar(listaID[position])
            }
            .setNeutralButton("Cancelar") { d, w -> }
            .show()

    }

    private fun llamarVentanaActualizar(idActualizar: String) {
        baseRemota.collection("restaurante")
            .document(idActualizar)
            .get()
            .addOnSuccessListener {
                var v = Intent(this,Main2Activity::class.java)

                v.putExtra("id",idActualizar)
                v.putExtra("nombre",it.getString("nombre"))
                v.putExtra("domicilio",it.getString("domicilio"))
                v.putExtra("telefono",it.getString("telefono"))
                v.putExtra("producto",it.getString("producto.producto"))
                v.putExtra("precio",it.getDouble("producto.precio"))
                v.putExtra("cantidad",it.getDouble("producto.cantidad"))
                v.putExtra("estado",it.getBoolean("producto.entregado"))
                startActivity(v)
            }
            .addOnFailureListener {
                Toast.makeText(this,"SE ELIMINO CORRECTAMENTE", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun eliminar(idEliminar: String) {
        baseRemota.collection("restaurante")
            .document(idEliminar)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this,"SE ELIMINO CORRECTAMENTE", Toast.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"NO SE PUDO ELIMINAR", Toast.LENGTH_LONG)
                    .show()
            }



    }

    private fun insertarRegistro() {

        var data = hashMapOf(
            "nombre" to nombre.text.toString(),
            "domicilio" to domicilio.text.toString(),
            "telefono" to celular.text.toString(),
            "producto" to hashMapOf(
                "producto" to producto.text.toString(),
                "precio" to precio.text.toString().toFloat(),
                "cantidad" to cantidad.text.toString().toFloat(),
                "entregado" to estado.isChecked

            )
        )

        baseRemota.collection("restaurante")
            .add(data as Any)
            .addOnSuccessListener {
                Toast.makeText(this,"SE CAPTURÓ", Toast.LENGTH_LONG)
                    .show()
                nombre.setText("");
                domicilio.setText("");
                celular.setText("");
                producto.setText("");
                precio.setText("");
                cantidad.setText("");
            }
            .addOnFailureListener {
                Toast.makeText(this," ERROR! NO SE CAPTURÓ", Toast.LENGTH_LONG)
                    .show()
            }

    }
}
