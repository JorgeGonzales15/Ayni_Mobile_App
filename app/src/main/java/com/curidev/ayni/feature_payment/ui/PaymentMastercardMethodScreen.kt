package com.curidev.ayni.feature_payment.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.curidev.ayni.feature_auth.data.repository.AuthRepository
import com.curidev.ayni.feature_auth.data.repository.UserRepository
import com.curidev.ayni.feature_auth.domain.model.User
import com.curidev.ayni.feature_order.data.repository.OrderRepository
import com.curidev.ayni.feature_order.data.repository.SaleRepository
import com.curidev.ayni.feature_order.domain.model.Order
import com.curidev.ayni.feature_order.domain.model.Sale
import com.curidev.ayni.shared.ui.inputtextfield.InputTextField_Payment
import com.curidev.ayni.shared.ui.topappbar.PrevNextTopAppBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentMastercardMethodScreen(
    id: Int,
    quantity: Int,
    navController: NavController,
    navigateToInvoice: (Int) -> Unit)
{
    val sale = remember { mutableStateOf<Sale?>(null) }

    SaleRepository().getSaleById(id) { retrievedProduct ->
        sale.value = retrievedProduct
    }

    val authRepository = AuthRepository()

    val userId = authRepository.getUserId()

    val user = remember {
        mutableStateOf<User?>(null)
    }

    if (userId != null) {
        UserRepository().getUserById(userId.toInt()) { retrievedUser ->
            user.value = retrievedUser
        }
    }

    user.value?.let {retrievedUser->
        Scaffold(
            topBar = {
                PrevNextTopAppBar("Checkout", navController)
            },
            bottomBar = {
                sale.value?.let {
                    BottomConfirmMastercardComponent(quantity, it, retrievedUser.id, navigateToInvoice)
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier
                .padding(paddingValues)
                .padding(25.dp, 0.dp)
            ) {
                MastercardFormComponent()
            }
        }
    }
}


@Composable
fun MastercardFormComponent() {
    var cardNumber = remember { mutableStateOf("") }
    var expirationDate = remember { mutableStateOf("") }
    var cvv = remember { mutableStateOf("") }
    var name = remember { mutableStateOf("") }
    var address = remember { mutableStateOf("") }
    var country = remember { mutableStateOf("") }
    var city = remember { mutableStateOf("") }
    var postalCode = remember { mutableStateOf("") }

    Text(
        text = "Payment Method Mastercard",
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        modifier = Modifier.paddingFromBaseline(0.dp, 40.dp))
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Mastercard", textAlign = TextAlign.Center)
        InputTextField_Payment(input = cardNumber, placeholder = "Card Number")
        Divider(modifier = Modifier.padding(5.dp))
        Row {
            InputTextField_Payment(input = expirationDate, placeholder = "Expiration Date", modifier = Modifier.width(200.dp))
            Spacer(modifier = Modifier.padding(5.dp))
            InputTextField_Payment(input = cvv, placeholder = "CVV", modifier = Modifier.width(150.dp))
        }
        Divider(modifier = Modifier.padding(5.dp))
        InputTextField_Payment(input = name, placeholder = "Name")
        Divider(modifier = Modifier.padding(5.dp))
        InputTextField_Payment(input = address, placeholder = "Address")
        Divider(modifier = Modifier.padding(5.dp))
        InputTextField_Payment(input = country, placeholder = "Country")
        Divider(modifier = Modifier.padding(5.dp))
        InputTextField_Payment(input = city, placeholder = "City")
        Divider(modifier = Modifier.padding(5.dp))
        InputTextField_Payment(input = postalCode, placeholder = "Postal Code")
    }
}

@Composable
fun BottomConfirmMastercardComponent(quantity: Int, sale: Sale, userId: String, navigateTo: (Int) -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                val order = createOrder(sale, quantity, userId)
                OrderRepository().createOrder(order) {order ->
                    navigateTo(order.id)
                }
                      },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp)) {
            Text(text = "Confirm Payment")
        }
    }
}

fun createOrder(sale: Sale, quantity: Int, userId: String): Order {
    val totalPrice = quantity.toDouble() * sale.unitPrice
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    return Order(
        id = 0,
        description = sale.description,
        totalPrice = totalPrice,
        quantity = quantity,
        paymentMethod = "Mastercard",
        saleId = sale.id,
        orderedBy = userId.toInt(),
        acceptedBy = sale.userId,
        orderedDate = currentDate,
        status = "pending"
    )
}

