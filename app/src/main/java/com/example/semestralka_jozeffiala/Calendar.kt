package com.example.semestralka_jozeffiala
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.semestralka_jozeffiala.databinding.FragmentKalendarBinding

/**
 * Trieda ktorá je fragmentom ktorý pracuje s kalendar widgetom na vybranie dátumu a pridavanie poznámok ku konkrétnemu dnu pomocou buttonu
 */
class Calendar : Fragment() {
    private lateinit var binding: FragmentKalendarBinding
    private var date : String = ""
    private var jeVybranyDatum = false
    private var pridajPoznamkuDialog: AlertDialog? = null //Alert dialog ktorý sa zobrazí pri prídávaní poznámky
    private var odstranenieDialog: AlertDialog? = null // Alert dialog ktorý sa zobrazí pri odstraňovaní poznámky
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKalendarBinding.inflate(layoutInflater) // inflate daný layout
        return binding.root //vráti koreňový View z inflatovaného zobrazenia fragmentu
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.calendarView.setOnDateChangeListener { _, rok, mesiac, den ->
            date = "$den/${mesiac + 1}/$rok"
            zobrazPoznamky(date)
            jeVybranyDatum = true
        }
        if (!jeVybranyDatum) {
            date = Date.getCurrentDay()
            zobrazPoznamky(date)
        }

        binding.buttonToAddNote.setOnClickListener {
            showPridajPoznamkuOkno(date)
        }
    }

    /**
     * Metóda ktorá slúži na zobrazenie okna na pridanie poznamky
     */
    private fun showPridajPoznamkuOkno(date: String) {
        // dialog pre pridanie poznamky
        val dialogView = layoutInflater.inflate(R.layout.oknopridaniepoznamky, null)
        // inicializácia textov pre poznámky
        val editTextPoznamka: EditText = dialogView.findViewById(R.id.editTextPoznamka)
        val editTextNoteCas: EditText = dialogView.findViewById(R.id.editTextNoteCas)
        //Vytvorenie dialogu použije ako layout oknopridaniepoznamky a po stlačení pridaj sa pridá poznamka do zoznamu
        val dialogBuilder = AlertDialog.Builder(dialogView.context)
            .setView(dialogView)
            .setPositiveButton("Pridaj") { dialog, _ ->
                val note = editTextPoznamka.text
                val note2 = editTextNoteCas.text
                val finaleNote = "$date - $note - $note2"
                // Add the note to the corresponding date's list of notes
                val notesList = DateData.listPoznamok.getOrPut(date) { mutableListOf() } // prida poznamku do zoznamu, buď dostane existují zoznam alebo vytvorí nový
                notesList.add(finaleNote) // pridanie poznamky
                zobrazPoznamky(date)
                dialog.dismiss() // zatvorenie dialogu
            }
        // Vytvorenie, nastavenie titulu a zobrazenie dialogu
        pridajPoznamkuDialog = dialogBuilder.create()
        pridajPoznamkuDialog!!.setTitle("Pridaj poznamku")
        pridajPoznamkuDialog!!.show()
    }

    /**
     * Zobrazuje poznámky priradené k danému dátumu.
     * Každá poznámka je reprezentovaná checkboxom a textovým polom.
     */
    private fun zobrazPoznamky(date: String) {
        binding.noteContainer.removeAllViews() // Odstráni všetky existujúce zobrazené poznámky
        val listpoznamok = DateData.listPoznamok.getOrDefault(date, mutableListOf()) // dostaň list poznamok
        if (listpoznamok.isNotEmpty()) { // ak nie je prázdny
            for (i in listpoznamok.indices) {
                // Vytvorenie potrebných inštančií
                val poznamkaLayout = LinearLayout(requireContext())
                val checkBoxPoznamka = CheckBox(requireContext())
                val poznamkaTextView = TextView(requireContext())
                poznamkaTextView.text = listpoznamok[i] // nastavenie textu poznamky na text z listu
                poznamkaTextView.textSize = 20f
                // Checkbox ak bude zaškrknutý tak vyskočí okno či chce používateľ vymazať poznamku ako vyberie ano tak ju vymaže ak vyberie nie tak sa nič nestane
                checkBoxPoznamka .setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                            .setTitle("Potvrdenie")
                            .setMessage("Chceš odstrániť tento predmet?")
                            .setPositiveButton("Ano") { dialog, _ ->
                                listpoznamok.removeAt(i) // odstránenie poznamky
                                zobrazPoznamky(date) // znovu zobrazenie po vymazaní
                                dialog.dismiss() //
                            }
                            .setNegativeButton("Nie") { dialog, _ ->
                                checkBoxPoznamka .isChecked = false // odškrkne sa pole
                                dialog.dismiss()
                            }
                        odstranenieDialog = dialogBuilder.create()
                        odstranenieDialog!!.show()
                    }
                }
                // Pridanie do poznamky do noteContainer(Linear layout)
                poznamkaLayout.addView(checkBoxPoznamka)
                poznamkaLayout.addView(poznamkaTextView)
                binding.noteContainer.addView(poznamkaLayout)
            }
        } else {
            // Ak pre daný dátum neexistujú žiadne poznámky tak sa zobrazí text že nemá žiadne poznámky
            val bezPoznamokTextView = TextView(requireContext())
            bezPoznamokTextView.text = getString(R.string.nie_su_poznamky)
            bezPoznamokTextView.textSize = 20f
            binding.noteContainer.addView(bezPoznamokTextView)
        }
    }

    /**
     * Metóda ktorá zatvorí dialógové okná v prípade, že sú aktuálne zobrazené
     */
    override fun onDestroyView() {
        super.onDestroyView()
        pridajPoznamkuDialog?.dismiss()
        odstranenieDialog?.dismiss()
    }
}
