package com.example.pitabmdmstudent.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class CountryCode(
    val name: String,
    val code: String
)

val countryCodes = listOf(
    CountryCode("India", "+91"),
    CountryCode("United States", "+1"),
    CountryCode("United Kingdom", "+44"),
    CountryCode("Canada", "+1"),
    CountryCode("Australia", "+61"),
    CountryCode("Germany", "+49"),
    CountryCode("France", "+33"),
    CountryCode("Japan", "+81"),
    CountryCode("China", "+86"),
    CountryCode("Brazil", "+55"),
    CountryCode("Russia", "+7"),
    CountryCode("South Korea", "+82"),
    CountryCode("Italy", "+39"),
    CountryCode("Spain", "+34"),
    CountryCode("Mexico", "+52"),
    CountryCode("Indonesia", "+62"),
    CountryCode("Netherlands", "+31"),
    CountryCode("Saudi Arabia", "+966"),
    CountryCode("Turkey", "+90"),
    CountryCode("Switzerland", "+41"),
    CountryCode("Poland", "+48"),
    CountryCode("Sweden", "+46"),
    CountryCode("Belgium", "+32"),
    CountryCode("Argentina", "+54"),
    CountryCode("Austria", "+43"),
    CountryCode("Norway", "+47"),
    CountryCode("United Arab Emirates", "+971"),
    CountryCode("Israel", "+972"),
    CountryCode("Singapore", "+65"),
    CountryCode("Hong Kong", "+852"),
    CountryCode("Denmark", "+45"),
    CountryCode("Malaysia", "+60"),
    CountryCode("Philippines", "+63"),
    CountryCode("South Africa", "+27"),
    CountryCode("Thailand", "+66"),
    CountryCode("Egypt", "+20"),
    CountryCode("Pakistan", "+92"),
    CountryCode("Bangladesh", "+880"),
    CountryCode("Vietnam", "+84"),
    CountryCode("Nigeria", "+234"),
    CountryCode("New Zealand", "+64"),
    CountryCode("Ireland", "+353"),
    CountryCode("Portugal", "+351"),
    CountryCode("Greece", "+30"),
    CountryCode("Czech Republic", "+420"),
    CountryCode("Romania", "+40"),
    CountryCode("Chile", "+56"),
    CountryCode("Colombia", "+57"),
    CountryCode("Peru", "+51"),
    CountryCode("Ukraine", "+380")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodePickerBottomSheet(
    isVisible: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit,
    onCountrySelected: (CountryCode) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCountries = if (searchQuery.isEmpty()) {
        countryCodes
    } else {
        countryCodes.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.code.contains(searchQuery)
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                searchQuery = ""
                onDismiss()
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Select Country",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    items(filteredCountries) { country ->
                        CountryCodeItem(
                            country = country,
                            onClick = {
                                searchQuery = ""
                                onCountrySelected(country)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CountryCodeItem(
    country: CountryCode,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = country.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = country.code,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
