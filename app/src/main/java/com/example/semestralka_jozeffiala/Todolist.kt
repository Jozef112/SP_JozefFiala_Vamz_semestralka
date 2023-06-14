package com.example.semestralka_jozeffiala

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.app.NotificationCompat

/**
 * Trieda ktorá je Adapter pre TodolistTriedu(fragment), služí na prácu s predmetmi v recyclerView
 */
class TodoListAdapter : RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {
    /**
     * Metóda ktorá vytvorí a vráti nový ViewHolder objekt pre každý riadok v RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todolist_poznamka, parent, false)
        return ViewHolder(view)
    }

    /**
     * Metóda ktorá prepojí data z položkou z recyclerView
     * Po kliknutí na tlačidlo sa poznámka vymaže
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ToDolistPoznamky.poznamkyList[position]
        holder.bind(item)
        holder.destroyButton.setOnClickListener {
            vymazPoznamku(holder.absoluteAdapterPosition)
        }
    }
    /**
     * Metóda na pridanie novej položky do zoznamu.
     */
    fun pridajPoznamku(poznamka: String) {
        ToDolistPoznamky.poznamkyList.add(poznamka)
        notifyItemInserted(ToDolistPoznamky.poznamkyList.size - 1)
    }

    /**
     * Metóda ktorá vráti počet poznámok v liste
     */
    override fun getItemCount(): Int {
        return ToDolistPoznamky.poznamkyList.size
    }
    /**
     * Metóda ktorá vymaže poznámku
     */
    private fun vymazPoznamku(position: Int) {
        ToDolistPoznamky.poznamkyList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Trieda ktorá predstavuje zobrazenie jednej položky v RecyclerView.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textTodoList) // poznamka
        val destroyButton: ImageButton = itemView.findViewById(R.id.deleteButton) // button pre odstranenie poznamky

        /**
         * Metóda ktorá nastaví textView na základe poznamky
         */
        fun bind(poznamka: String) {
            textView.text = poznamka
        }
    }
}

/**
 * Trieda ktorá predstavuje Todolist
 * Obsahuje recyclerView do ktorého sa pridávajú poznamky po kliknutí na Floating ActionButton. Po kliknutí na tlačidlo sa presunie použivateľ do aktivity na pridanie poznamky
 *
 */
class Todolist : Fragment() {
    private lateinit var todoListAdapter: TodoListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) { // Aktivita pre pridanie poznamky prebehla úspešne
                val data: Intent? = result.data
                if (data != null) {
                    // priradenie hodnôť ktoré boli zadané pri aktivite pre pridanie do listu
                    val poznamka = data.getStringExtra(PridanieDoToDolistu.POZNAMKA)
                    val date = data.getStringExtra(PridanieDoToDolistu.DATUM)
                    val cas = data.getStringExtra(PridanieDoToDolistu.CAS)

                    // Process the received title and date here (e.g., add to the list adapter)
                    todoListAdapter.pridajPoznamku("$date - $cas - $poznamka") // pridanie poznamky
                    //vytvorenie notifikačného kanála
                    val context = requireContext()
                    createNotificationChannel(context) // vytvorenie notifikačného kanála
                    if (poznamka != null && date != null && cas != null) {
                        val calendar = java.util.Calendar.getInstance().apply {
                            timeInMillis = System.currentTimeMillis()
                            set(java.util.Calendar.HOUR_OF_DAY, cas.split(":")[0].toInt() - 1)
                            set(java.util.Calendar.MINUTE, cas.split(":")[1].toInt())
                            set(java.util.Calendar.SECOND, 0)
                        }
                        scheduleNotificationWith1HourLeft(context, poznamka, calendar.timeInMillis)
                    }
                }
            }
        }

    /**
     * Metóda ktorá vytvára a inicializuje zobrazenie fragmentu.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todolist, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        addButton = view.findViewById(R.id.floatingActionButton)

        todoListAdapter = TodoListAdapter()

        recyclerView.adapter = todoListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Po kliknutí na tlačidlo sa spustí aktivita
        addButton.setOnClickListener {
            val intent = Intent(requireContext(), PridanieDoToDolistu::class.java)
            startForResult.launch(intent)
        }

        return view
    }

    /**
     * Metóda na vytvorenie notifikačného kanálu.
     */
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // najprv to skontroluje či sa to spušta na Android Oreo a novšie, notifikácie dostupne až od tejto verzie
            val channel = NotificationChannel("1_hour_channel", "1 Hour Reminder", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Notification channel for 1 hour reminders"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java) // dostanie inštancie Notification managera
            notificationManager?.createNotificationChannel(channel) // vytvorenie notifikačného kanála
        }
    }

    /**
     * Metóda na naplánovanie notifikácie s upozornením na 1 hodinu pred udalostou
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun scheduleNotificationWith1HourLeft(context: Context, item: String, timeInMillis: Long) {
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_TEXT, item)
        //vytvorenie pendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // inicializácia alarmManagera
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent) //  AlarmManager.RTC_WAKEUP je typ alarmu ktorý zobudí zariadnie
        // timeinMillis je čas kedy sa alarm má zapnúť a pendingIntent že sa Broadcastne
    }
}

/**
 * Trieda, ktorá slúži na prijímanie a zobrazovanie notifikácií.
 */
class NotificationReceiver : BroadcastReceiver() {
        companion object {
            const val NOTIFICATION_TEXT = "notification_text"
        }

    /**
     * Metóda, ktorá je vyvolaná pri prijatí broadcastovej správy.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                val notificationText = intent.getStringExtra(NOTIFICATION_TEXT) // notifikačný_text dostane parametre z intent
                if (notificationText != null) {
                    showNotification(context, notificationText) // zobrazenie notifikácie
                }
            }
        }
    /**
     * Metóda na zobrazenie notifikácie
     * Vytvorí objekt NotificationCompat.Builder s potrebnými parametrami.
     */
    @SuppressLint("MissingPermission")
        private fun showNotification(context: Context, notificationText: String) {
            //vytvorenie notificationBuildera
            val notificationBuilder = NotificationCompat.Builder(context,"1_hour_channel")
                .setContentTitle("1 hodinové upozornenie")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.baseline_event_note_24)

            val notificationManagerCompat = NotificationManagerCompat.from(context) // ziskanie inštancie NotificationManagerCompat
            notificationManagerCompat.notify(1, notificationBuilder.build()) // ukázanie notifikácie
        }
    }