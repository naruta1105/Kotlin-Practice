package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private const val PRICE_PER_CUPCAKE = 2.00
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00
private val allFlavorsList: List<String> = listOf("Vanilla","Chocolate","Red Velvet",
"Salted Caramel","Coffee","Lemon Blueberry(Special)")

class OrderViewModel : ViewModel() {
    // số lượng cupcakes
    private val _quantity = MutableLiveData<Int>()
    val quantity : LiveData<Int> = _quantity

    // số lượng cupcakes chưa đc chọn flavor
    private val _qualityRemain = MutableLiveData<Int>()
    val qualityRemain : LiveData<Int> = _qualityRemain

    // Dict <cupcakes flavor, flavor quantity>
    private val _flavors = MutableLiveData<MutableMap<String, String>>()
    val flavors : LiveData<MutableMap<String, String>> = _flavors

    // Nhừng flavor đã được chọn
    private val _flavorsChecked = MutableLiveData<MutableList<String>>()
    val flavorsChecked : LiveData<MutableList<String>> = _flavorsChecked

    // Ngày đặt
    private val _date = MutableLiveData<String>()
    val date : LiveData<String> = _date

    // Tên khách
    private val _customer = MutableLiveData<String>()
    val customer : LiveData<String> = _customer

    // Format giá cả
    private val _price = MutableLiveData<Double>()
    val price : LiveData<String> = Transformations.map(_price) {
        val lc = Locale("vi", "VN") //Định dạng locale việt nam
        NumberFormat.getCurrencyInstance(lc).format(it)
    }

    // danh sách date
    val dateOptions = getPickupOptions()

    init {
        resetOrder()
    }

    fun setQuantity(numberCupcakes: Int){
        /*
        Kiểm tra khi thay đổi số lượng cupcakes có vượt qua số đã chọn ko
        nếu số cupcake đã chọn vượt qua số lượng cupcake mới thì reset lại toàn bộ
         */
        if (numberCupcakes< ((_quantity.value?:0)-(_qualityRemain.value?:0))) {
            emptyFlavorQuantity()
            _qualityRemain.value = numberCupcakes
        } else {
            _qualityRemain.value = (_qualityRemain.value?:0) + (numberCupcakes-(_quantity.value?:0))
        }
        _quantity.value = numberCupcakes
        updatePrice()
    }

    fun setFlavor(desiredFlavor: String) {
        val currentFlavor = _flavors.value?: mutableMapOf<String, String>()
        var currentQualityRemain = _qualityRemain.value?:0
        var currentFlavorChosen = _flavorsChecked.value?: mutableListOf<String>()
        if (currentFlavor.contains(desiredFlavor)) {
            if (currentFlavorChosen.contains(desiredFlavor)) {
                currentQualityRemain += currentFlavor[desiredFlavor]!!.toInt()
                currentFlavor[desiredFlavor] = "0"
                currentFlavorChosen.remove(desiredFlavor)
            } else if (currentQualityRemain>0) {
                    currentFlavor[desiredFlavor] = "1"
                    currentQualityRemain--
                    currentFlavorChosen.add(desiredFlavor)
                }
        } else {
            currentFlavor.put(desiredFlavor,"1")
            currentQualityRemain--
            currentFlavorChosen.add(desiredFlavor)
        }
        _flavorsChecked.value = currentFlavorChosen
        _qualityRemain.value = currentQualityRemain
        _flavors.value = currentFlavor
    }

    fun setDate(pickupDate: String) {
        _date.value = pickupDate
        updatePrice()
    }

    fun setName(customerName: CharSequence) {
        _customer.value = customerName.toString()
    }

    fun hasNoFlavorSet(): Boolean {
        // flavor is map so use any() to check empty
        return  (_flavorsChecked.value?: mutableListOf<String>()).isEmpty()
    }

    private fun emptyFlavorQuantity() {
        val currentflavor = mutableMapOf<String, String>()
        for (key in allFlavorsList) {
            currentflavor.put(key,"0")
        }
        _flavorsChecked.value = mutableListOf<String>()
        _flavors.value = currentflavor
    }


    fun updateFlavorsQuantity(desiredFlavor : String, plus : Boolean) {
        val currentflavor = _flavors.value?: mutableMapOf<String, String>()
        var currentQualityRemain = _qualityRemain.value?:0
        if (currentflavor.contains(desiredFlavor)) {
            if (currentQualityRemain>0 && plus) {
                currentflavor[desiredFlavor] = (currentflavor[desiredFlavor]!!.toInt()+1).toString()
                currentQualityRemain--
            }
            if ((!currentflavor[desiredFlavor].equals("0"))&&(!plus)) {
                currentflavor[desiredFlavor] = (currentflavor[desiredFlavor]!!.toInt()-1).toString()
                currentQualityRemain++
        } }
        if (currentflavor[desiredFlavor].equals("0")) {
            _flavorsChecked.value?.remove(desiredFlavor)
        }
        _qualityRemain.value = currentQualityRemain
        _flavors.value = currentflavor
    }

    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        // Get today
        val calendar = Calendar.getInstance()
        // 4 day include today
        repeat(4) {
            options.add(formatter.format(calendar.time))
            // increase day by 1
            calendar.add(Calendar.DATE, 1)
        }
        return options
    }

    private fun updatePrice() {
        // if quantity.value is null then use 0 else quantity.value
        var calculatedPrice = (quantity.value ?: 0) * PRICE_PER_CUPCAKE
        // If the user selected the first option (today) for pickup, add the surcharge
        if (dateOptions[0] == _date.value) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatedPrice
    }

    fun resetOrder() {
        _quantity.value = 0
        _qualityRemain.value = 0
        emptyFlavorQuantity()
        _date.value = dateOptions[0]
        _price.value = 0.0
        _customer.value = ""
    }


}