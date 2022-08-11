package com.example.todo_application

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.activity_task.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

const val Name="todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var myCalendar: Calendar
    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    var finalDate = 0L
    var finalTime = 0L

    val labels= arrayListOf<String>("Personal","Business","Insurance","Banking","Shopping","Misclenious")

    val db by lazy {
        AppDatabase.getDatabase(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

       dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        setUpSpinner()
    }

    private fun setUpSpinner() {
        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,labels)
        labels.sort()
        spinnerCategory.adapter=adapter
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.dateEdt ->{
                setListener()
            }
            R.id.timeEdt ->{
                setTimeListener()
            }
            R.id.saveBtn ->{
                saveTodo()
            }
        }
    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInpLay.editText?.text.toString()
        val description = taskInpLay.editText?.text.toString()

        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                return@withContext db.todoDao().insertTask(
                    TodoModel(
                        title,
                        description,
                        category,
                        finalDate,
                        finalTime
                    )
                )
            }
            finish()
        }
    }

    private fun setTimeListener() {
        myCalendar=Calendar.getInstance()

         timeSetListener=TimePickerDialog.OnTimeSetListener{_:TimePicker,hourOfDay:Int,min:Int ->
            myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
            myCalendar.set(Calendar.MINUTE,min)
            updateTime()
        }

        val timePicker=TimePickerDialog(this,timeSetListener,myCalendar.get(Calendar.HOUR_OF_DAY)
            ,myCalendar.get(Calendar.MINUTE),false)
        timePicker.show()

    }

    private fun updateTime() {
       val myformat="h:mm a"
        val stf=java.text.SimpleDateFormat(myformat)
        timeEdt.setText(stf.format(myCalendar.time))


    }

    private fun setListener() {
        myCalendar=Calendar.getInstance()
        dateSetListener=DatePickerDialog.OnDateSetListener{_:DatePicker,year:Int,month :Int,dayOfMonth :Int ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }
        val datePickerDialog=DatePickerDialog(this,dateSetListener,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.datePicker.minDate=System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myFormat="EEE, d MMM yyyy"
        val sdf=java.text.SimpleDateFormat(myFormat)
        dateEdt.setText(sdf.format(myCalendar.time))

       timeInptLay.visibility=View.VISIBLE
    }
}