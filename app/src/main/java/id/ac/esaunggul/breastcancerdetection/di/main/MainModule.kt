/*
 * Copyright 2020 Angga Satya Putra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.ac.esaunggul.breastcancerdetection.di.main

import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.data.repo.MainRepo
import java.util.Calendar
import java.util.TimeZone

@Module
object MainModule {

    @MainScope
    @Provides
    fun provideDatePicker(): MaterialDatePicker<Long> {
        val builder = MaterialDatePicker.Builder.datePicker()
        val constraint = CalendarConstraints.Builder()

        val date = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        date.add(Calendar.MONTH, 1)
        val maxDate = date.timeInMillis

        val dateValidatorMin = DateValidatorPointForward.now()
        val dateValidatorMax = DateValidatorPointBackward.before(maxDate)

        val listValidators = mutableListOf<CalendarConstraints.DateValidator>()
        listValidators.add(dateValidatorMin)
        listValidators.add(dateValidatorMax)

        val validators = CompositeDateValidator.allOf(listValidators)
        constraint.setValidator(validators)

        builder.setTitleText(R.string.form_date_hint)
        builder.setCalendarConstraints(constraint.build())

        return builder.build()
    }

    @MainScope
    @Provides
    fun provideMainRepo(auth: FirebaseAuth, database: FirebaseFirestore): MainRepo =
        MainRepo(auth, database)
}