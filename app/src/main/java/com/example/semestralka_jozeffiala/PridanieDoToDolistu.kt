package com.example.semestralka_jozeffiala

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CalendarView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.semestralka_jozeffiala.databinding.ActivityPridanieDoToDolistuBinding

/**
 * Trieda ktorá je aktivitou na pridávanie poznámky do todolistu.
 */
class PridanieDoToDolistu : AppCompatActivity() {
    // Object ktorý obsahuje hodnoty presun do Todolistu a ULOZENYCAS,ULOZENYDATUM pre dialog okna
    companion object {
        const val POZNAMKA = "poznamka"
        const val DATUM = "datum"
        const val CAS = "cas"
        const val ULOZENYCAS = "ulozeny_cas"
        const val ULOZENYDATUM = "ulozeny_datum"
    }
    private var date: String = "Vyber si dátum pre poznámku" // default čo sa napíše keď používateľ nevyberie čas
    private var time: String = "Vyber si svoj čas" // default čo sa napíše keď používateľ nevyberie poznámku
    private lateinit var binding: ActivityPridanieDoToDolistuBinding
    private var vyberCasDialog: AlertDialog? = null
    private var vyberDatumDialog: AlertDialog? = null

    /**
     * Metóda ktorá sa volá pri vytvoreni aktivity.
     * Zobrazuje CalendarView pri kliknutí na imageButtonVyberDatum a TimePicker pri kliknutí na imageButtonVyberCas.
     * Po kliknutí na Button_to_add_note sa uložia údaje ktoré si používateľ vybral a posunie výsledok aktivity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPridanieDoToDolistuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Ak uložené inštancie existujú tak si ich nastav ako boli
        if (savedInstanceState != null) {
            time = savedInstanceState.getString(ULOZENYCAS, "")
            date = savedInstanceState.getString(ULOZENYDATUM,"")
            binding.textViewTime.text = time
            binding.textViewDate.text = date
        }
        // Po kliknutí na imageButton sa zobrazí kalendar widget
        binding.imageButtonVyberDatum.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.todolist_calendar_okno, null) // layout
            val calendarView = dialogView.findViewById<CalendarView>(R.id.calendarView) // Kalendar widget
            date = Date.getCurrentDay() // metóda na získanie aktúalneho dňa
            //listener ktorý zistí na ktorý deň používateľ klikol
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                date = "$dayOfMonth/${month + 1}/$year"
            }
            // dialog v ktorom keď použivateľ klikne OK tak vyberie dátum ak Cancel tak sa dátum nevyberie
            val dialog = AlertDialog.Builder(this)
                .setTitle("Select Date")
                .setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    binding.textViewDate.text = date // pridanie dátumu
                }
                .setNegativeButton("Cancel", null) // nič sa nestane
            vyberDatumDialog = dialog.create()
            vyberDatumDialog?.show()
        }
        // Po kliknutí na imageButton sa zobrazí timePicker v ktorom si používateľ vyberie svoj čas
        binding.imageButtonVyberCas.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.todolist_vybercas_okno, null)
            val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
            // Ak používateľ klikne ok tak sa vyberie čas a zapíše sa
            val dialog = AlertDialog.Builder(this)
                .setTitle("Select Time")
                .setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    Cas.hodiny = timePicker.hour
                    Cas.minuty = timePicker.minute
                    time = String.format("%02d:%02d", Cas.hodiny, Cas.minuty) // formát výpisu
                    binding.textViewTime.text = time // priradnie času
                }
                .setNegativeButton("Cancel") { _, _ ->
                }

            // vytvorenie a ukázanie dialogu
            vyberCasDialog = dialog.create()
            vyberCasDialog?.show()
        }
        // Ak používateľ vyplnil všetky polia tak po kliknutí na button sa ukončí aktivita ako OK
        binding.buttonToAddNoteTodolist.setOnClickListener {
            val poznamka = binding.titleEditText.text.toString().trim()
            if (poznamka.isNotEmpty() && date != "Vyber si dátum pre poznámku"  && time != "Vyber si svoj čas" ) {
                val resultIntent = Intent().apply {
                    putExtra(POZNAMKA, poznamka)
                    putExtra(DATUM, date)
                    putExtra(CAS,time)
                }
                setResult(Activity.RESULT_OK, resultIntent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }

    /**
     *  Metóda ktorá zatvorí dialógové okná v prípade, že sú aktuálne zobrazené
     */
    override fun onDestroy() {
        super.onDestroy()
        vyberCasDialog?.dismiss()
        vyberDatumDialog?.dismiss()
    }

    /**
     * Metóda na uloženie instancií aby pri otočení cas a dátum boli stále prítomný na obrazovke
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ULOZENYCAS, time)
        outState.putString(ULOZENYDATUM,date)
    }
}