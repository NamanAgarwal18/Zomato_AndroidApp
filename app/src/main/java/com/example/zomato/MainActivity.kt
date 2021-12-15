package com.example.zomato

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import io.realm.*
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.sync.SyncConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.restaurant_adapter.view.*
import org.bson.BSONObject
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.types.ObjectId
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.DocumentType
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class MainActivity : AppCompatActivity() {
    val realmName: String = "zomato-dztkd"

    lateinit var cuisines:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        var tog = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Realm.init(this)
        val adapter = GroupAdapter<GroupieViewHolder>()
        val appID : String = realmName;
        val app = App(
            AppConfiguration.Builder(appID)
            .build())

        val anonymousCredentials: Credentials = Credentials.anonymous()
        val credentials: Credentials = Credentials.anonymous()
        cuisines = arrayListOf()

        app.loginAsync(credentials) {
            if (it.isSuccess) {
                Log.d("User", "Successfully authenticated anonymously.")
                val user: User? = app.currentUser()

                val partitionValue: String = "My Project"
  //              val config = SyncConfiguration.Builder(user, partitionValue).build()

//                val uiThreadRealm = Realm.getInstance(config)

//                addChangeListenerToRealm(uiThreadRealm)
//
//                val task : FutureTask<String> = FutureTask(BackgroundQuickStart(app.currentUser()!!), "test")
//                val executorService: ExecutorService = Executors.newFixedThreadPool(2)
//                executorService.execute(task)

            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: ${it.error}")
            }
        }

        val user = app.currentUser()
        val mongoClient = user!!.getMongoClient("mongodb-atlas")
        val mongoDatabase = mongoClient.getDatabase("Zomato")
        val pojoCodecRegistry = CodecRegistries.fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY, CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))
        val mongoCollection = mongoDatabase.getCollection("Restaurants", Rest::class.java).withCodecRegistry(pojoCodecRegistry)


        btnAdd.setOnClickListener {
            rvRestaurent.visibility = View.GONE
            btnAdd.visibility = View.GONE
            btnAnalytics.visibility = View.GONE
            AddRestaurent.visibility = View.VISIBLE
            cuisines.clear()
        }

        btnBack.setOnClickListener {
            rvRestaurent.visibility = View.VISIBLE
            btnAdd.visibility = View.VISIBLE
            btnAnalytics.visibility = View.VISIBLE
            AddRestaurent.visibility = View.GONE
            cuisines.clear()
        }

        loadFeed(adapter, mongoCollection,app)



        btnAmerican.setOnClickListener {
            Log.d("User", "${etTo.text.toString()}")
            if("American" in cuisines)
            {
                cuisines.remove("American")
                btnAmerican.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("American")
                btnAmerican.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }

        btnAnalytics.setOnClickListener {
            ivCuisines.visibility= View.VISIBLE
            rvRestaurent.visibility = View.GONE
            btnAdd.visibility = View.GONE
            btnAnalytics.visibility = View.GONE
            btnNextGraph.visibility = View.VISIBLE
            btnBackGraph.visibility = View.VISIBLE

            ivCuisines.webViewClient = WebViewClient()


            ivCuisines.loadUrl("https://charts.mongodb.com/charts-project-0-bmxqj/embed/charts?id=7b0ed5a9-27e5-41c4-bef6-ab882d782abb&maxDataAge=10&theme=light&autoRefresh=true")

            ivCuisines.settings.javaScriptEnabled = true

            ivCuisines.settings.setSupportZoom(true)


        }

        btnBackGraph.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnNextGraph.setOnClickListener {
            if(tog)
            {
                tog = false
                ivCuisines.webViewClient = WebViewClient()


                ivCuisines.loadUrl("https://charts.mongodb.com/charts-project-0-bmxqj/embed/charts?id=7b0ed5a9-27e5-41c4-bef6-ab882d782abb&maxDataAge=10&theme=light&autoRefresh=true")

                ivCuisines.settings.javaScriptEnabled = true

                ivCuisines.settings.setSupportZoom(true)
            }
            else
            {
                tog = true
                ivCuisines.webViewClient = WebViewClient()


                ivCuisines.loadUrl("https://charts.mongodb.com/charts-project-0-bmxqj/embed/charts?id=d45740fd-2223-48b7-8ab6-0e274b78e5a8&maxDataAge=10&theme=light&autoRefresh=true")

                ivCuisines.settings.javaScriptEnabled = true

                ivCuisines.settings.setSupportZoom(true)
            }
        }

        btnArabian.setOnClickListener {
            if("Arabian" in cuisines)
            {
                cuisines.remove("Arabian")
                btnArabian.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("Arabian")
                btnArabian.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnAsian.setOnClickListener {
            if("Asian" in cuisines)
            {
                cuisines.remove("Asian")
                btnAsian.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("Asian")
                btnAsian.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnBakery.setOnClickListener {
            if("Bakery" in cuisines)
            {
                cuisines.remove("Bakery")
                btnBakery.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("Bakery")
                btnBakery.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnBiryani.setOnClickListener {
            if("Biryani" in cuisines)
            {
                cuisines.remove("Biryani")
                btnBiryani.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("Biryani")
                btnBiryani.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnCafe.setOnClickListener {
            if("Cafe" in cuisines)
            {
                cuisines.remove("Cafe")
                btnCafe.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("Cafe")
                btnCafe.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnChinese.setOnClickListener {
            if("Chinese" in cuisines)
            {
                cuisines.remove("Chinese")
                btnChinese.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("Chinese")
                btnChinese.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnContinental.setOnClickListener {
            if("Continental" in cuisines)
            {
                cuisines.remove("Continental")
                btnContinental.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("Continental")
                btnContinental.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnFastFood.setOnClickListener {
            if("Fast Food" in cuisines)
            {
                cuisines.remove("Fast Food")
                btnFastFood.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("Fast Food")
                btnFastFood.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnSeaFood.setOnClickListener {
            if("Sea Food" in cuisines)
            {
                cuisines.remove("Sea Food")
                btnSeaFood.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("Sea Food")
                btnSeaFood.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnNorthIndian.setOnClickListener {
            if("North" in cuisines)
            {
                cuisines.remove("North")
                btnNorthIndian.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("North")
                btnNorthIndian.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }
        btnSouthIndian.setOnClickListener {
            if("South" in cuisines)
            {
                cuisines.remove("South")
                btnSouthIndian.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }
            else
            {
                cuisines.add("South")
                btnSouthIndian.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_700))
            }
        }

        btnUpload.setOnClickListener {
            if(cuisines.size==0||etCost.text.isEmpty()||etFrom.text.isEmpty()||etTo.text.isEmpty()||etName.text.isEmpty())
            {
                Toast.makeText(this,"Fields Not Filled",Toast.LENGTH_LONG).show()
            }
            else
            {

                var aa = RealmList<String>()
                for(a in cuisines)
                {
                    aa.add(a)
                }


                var link = "www.zomato.com/hydrabad/"
                val words = etName.text.toString().split("\\s+".toRegex())
                for(word in words)
                {
                    link+=word
                }
                var obj = Rest(etName.text.toString(),link,etCost.text.toString().toInt(),etFrom.text.toString(), etTo.text.toString(),aa)
                mongoCollection?.insertOne(obj)?.getAsync{task ->
                    if(task.isSuccess)
                    {
                        Log.d("User", "Restaurant Uploaded Successfully")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    }
                    else
                    {
                        Log.d("User", "Restaurant can not be uploaded ${task.error}")
                    }
                }
            }
        }
    }

    private fun loadFeed(adapter: GroupAdapter<GroupieViewHolder>, mongoCollection: MongoCollection<Rest>,app: App)
    {
        val findTask = mongoCollection.find().iterator()
        findTask.getAsync { task->
            if(task.isSuccess)
            {
                val results = task.get()
                Log.d("User", "successfully found")
                while (results.hasNext()) {
                    val ab: Rest = results.next()
                    adapter.add(AdapterClass(ab,app))
                }
                rvRestaurent.adapter = adapter
            }
            else
            {
                Log.d("User", "failed to find documents with: ${task.error}")
            }
        }
    }
}


open class Rest: RealmModel {
    var Name: String = ""
    var Link: String = ""
    var Cost: Int = 0
    var From: String = ""
    var To:String = ""
    var Cuisines: RealmList<String>? = null

    constructor(name: String,link: String,Cost: Int,From: String,To:String,Cuisines: RealmList<String>)
    {
        this.Name = name
        this.Link = link
        this.Cost = Cost
        this.From = From
        this.To = To
        this.Cuisines = Cuisines
    }

    override fun toString(): String {
        var str = "Name -> $Name \n" +
                "Link -> $Link \n" +
                "Cost -> $Cost \n" +
                "From -> $From and To -> $To \n" +
                "Cuisines"
        Log.d("User", "")
        for(a in Cuisines!!)
        {
           str += a + "\n"
        }
        return str
    }

    constructor() {}
}

class AdapterClass(val obj: Rest , app: App): Item<GroupieViewHolder>(){

    val app = App(AppConfiguration.Builder("zomato-dztkd").build())
    val user = app.currentUser()
    val mongoClient = user!!.getMongoClient("mongodb-atlas")
    val mongoDatabase = mongoClient.getDatabase("Zomato")
    val pojoCodecRegistry = CodecRegistries.fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY, CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))
    val mongoCollection = mongoDatabase.getCollection("Restaurants", Rest::class.java).withCodecRegistry(pojoCodecRegistry)

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tvRestaurantNameRes.text = obj.Name
        var str = ""
        val start: List<String> = obj.From.split(":").map { it -> it.trim() }
        val end: List<String> = obj.To.split(":").map { it -> it.trim() }

        var arr = ArrayList<String>()


        if(start[0].toInt()<12)
        {
            str+=obj.From + "AM - "
        }
        else if(start[0].toInt() == 12)
        {
            str+=obj.From + "PM - "
        }
        else
        {
            str+= (start[0].toInt() - 12).toString() + ":" + start[1] + "PM - "
        }

        if(end[0].toInt()<12)
        {
            str+= obj.To +"AM"
        }
        else if(end[0].toInt()==12)
        {
            str+= obj.To +"PM"
        }
        else
        {
            str+= (end[0].toInt() - 12).toString() + ":" + end[1] + "PM"
        }

        viewHolder.itemView.tvTimimgRes.text = str
        str = "(" + obj.Cost + " For 2)"
        viewHolder.itemView.tvCostRes.text = str
        str = ""
        var i = 0;
        while(i<obj.Cuisines!!.size)
        {
            var a = obj.Cuisines!![i]
            if(a=="North")
            {
                str+="North Indian"
            }
            else if(a=="South")
            {
                str+="South Indian"
            }
            else
            {
                str+=a.toString()
            }
            i++
            if(i<obj.Cuisines!!.size)
            {
                str+=", "
            }
        }
        viewHolder.itemView.tvCuisineRes.text = str



        viewHolder.itemView.btnEditRes.setOnClickListener {
            arr.clear()
            for(a in obj.Cuisines!!)
            {
                arr.add(a)
            }

            viewHolder.itemView.RL.visibility = View.GONE
            viewHolder.itemView.RLUpdate.visibility = View.VISIBLE
            viewHolder.itemView.etRestaurantNameRes.setText(obj.Name.toString())
            viewHolder.itemView.etCostRes.setText(obj.Cost.toString())
            viewHolder.itemView.etStartTimimgRes.setText(obj.From)
            viewHolder.itemView.etEndTimimgRes.setText(obj.To)
            if("Chinese" in arr)
            {
                viewHolder.itemView.etChineseRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("American" in arr)
            {
                viewHolder.itemView.etAmericanRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("Arabian" in arr)
            {
                viewHolder.itemView.etArabianRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("Bakery" in arr)
            {
                viewHolder.itemView.etBakeryRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("North" in arr)
            {
                viewHolder.itemView.etNorthIndianRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("South" in arr)
            {
                viewHolder.itemView.etSouthIndian.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("Asian" in arr)
            {
                viewHolder.itemView.etAsianRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("Sea Food" in arr)
            {
                viewHolder.itemView.etSeaFoodRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("Biryani" in arr)
            {
                viewHolder.itemView.etBiryaniRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("Cafe" in arr)
            {
                viewHolder.itemView.etCafeRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("Fast Food" in arr)
            {
                viewHolder.itemView.etFastFoodRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }
            if("Continental" in arr)
            {
                viewHolder.itemView.etContinentalRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
            }

        }

        viewHolder.itemView.etChineseRes.setOnClickListener {
            if("Chinese" in arr)
            {
                viewHolder.itemView.etChineseRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("Chinese")
            }
            else
            {
                viewHolder.itemView.etChineseRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("Chinese")
            }
        }

        viewHolder.itemView.etAmericanRes.setOnClickListener {
            if("American" in arr)
            {
                viewHolder.itemView.etAmericanRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("American")
            }
            else
            {
                viewHolder.itemView.etAmericanRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("American")
            }
        }
        viewHolder.itemView.etArabianRes.setOnClickListener {
            if("Arabian" in arr)
            {
                viewHolder.itemView.etArabianRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("Arabian")
            }
            else
            {
                viewHolder.itemView.etArabianRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("Arabian")
            }
        }
        viewHolder.itemView.etBakeryRes.setOnClickListener {
            if("Bakery" in arr)
            {
                viewHolder.itemView.etBakeryRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("Bakery")
            }
            else
            {
                viewHolder.itemView.etBakeryRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("Bakery")
            }
        }
        viewHolder.itemView.etNorthIndianRes.setOnClickListener {
            if("North" in arr)
            {
                viewHolder.itemView.etNorthIndianRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("North")
            }
            else
            {
                viewHolder.itemView.etNorthIndianRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("North")
            }
        }
        viewHolder.itemView.etSouthIndian.setOnClickListener {
            if("South" in arr)
            {
                viewHolder.itemView.etSouthIndian.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("South")
            }
            else
            {
                viewHolder.itemView.etSouthIndian.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("South")
            }
        }
        viewHolder.itemView.etAsianRes.setOnClickListener {
            if("Asian" in arr)
            {
                viewHolder.itemView.etAsianRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("Asian")
            }
            else
            {
                viewHolder.itemView.etAsianRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("Asian")
            }
        }
        viewHolder.itemView.etSeaFoodRes.setOnClickListener {
            if("Sea Food" in arr)
            {
                viewHolder.itemView.etSeaFoodRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("Sea Food")
            }
            else
            {
                viewHolder.itemView.etSeaFoodRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("Sea Food")
            }
        }
        viewHolder.itemView.etBiryaniRes.setOnClickListener {
            if("Biryani" in arr)
            {
                viewHolder.itemView.etBiryaniRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("Biryani")
            }
            else
            {
                viewHolder.itemView.etBiryaniRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("Biryani")
            }
        }
        viewHolder.itemView.etCafeRes.setOnClickListener {
            if("Cafe" in arr)
            {
                viewHolder.itemView.etCafeRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("Cafe")
            }
            else
            {
                viewHolder.itemView.etCafeRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("Cafe")
            }
        }
        viewHolder.itemView.etFastFoodRes.setOnClickListener {
            if("Fast Food" in arr)
            {
                viewHolder.itemView.etFastFoodRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("Fast Food")
            }
            else
            {
                viewHolder.itemView.etFastFoodRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("Fast Food")
            }
        }
        viewHolder.itemView.etContinentalRes.setOnClickListener {
            if("Continental" in arr)
            {
                viewHolder.itemView.etContinentalRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.purple_700))
                arr.remove("Continental")
            }
            else
            {
                viewHolder.itemView.etContinentalRes.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context,R.color.black))
                arr.add("Continental")
            }
        }

        viewHolder.itemView.etBackRes.setOnClickListener {
            viewHolder.itemView.RLUpdate.visibility = View.GONE
            viewHolder.itemView.RL.visibility = View.VISIBLE

        }

        viewHolder.itemView.btnDeleteRes.setOnClickListener {
            val queryFilter = Document("name", obj.Name)
            mongoCollection.deleteOne(queryFilter).getAsync { task ->
                if (task.isSuccess)
                {
                    val count = task.get().deletedCount
                    if (count == 1L) {
                        Log.d("User", "successfully deleted a document.")
                        val intent = Intent(viewHolder.itemView.context, MainActivity::class.java)
                        viewHolder.itemView.context.startActivity(intent)
                    }
                    else {
                        Log.d("User", "did not delete a document.")
                    }
                }
                else
                {
                    Log.e("EXAMPLE", "failed to delete document with: ${task.error}")
                }
            }

        }

        viewHolder.itemView.etUpdateRes.setOnClickListener {
            val queryFilter = Document("name", obj.Name)
            mongoCollection.deleteOne(queryFilter).getAsync { task ->
                if (task.isSuccess) {
                    val count = task.get().deletedCount
                    if (count == 1L) {
                        Log.d("User", "successfully deleted a document.")
                        if(arr.size==0||viewHolder.itemView.etCostRes.text.isEmpty()||viewHolder.itemView.etStartTimimgRes.text.isEmpty()||viewHolder.itemView.etEndTimimgRes.text.isEmpty()||viewHolder.itemView.etRestaurantNameRes.text.isEmpty())
                        {
                            Toast.makeText(viewHolder.itemView.context,"Fields Not Filled",Toast.LENGTH_LONG).show()
                        }
                        var aa = RealmList<String>()
                        for(a in arr)
                        {
                            aa.add(a)
                        }
                        var link = "www.zomato.com/hydrabad/"
                        val words = viewHolder.itemView.etRestaurantNameRes.text.toString().split("\\s+".toRegex())
                        for(word in words)
                        {
                            link+=word
                        }
                        var obj = Rest(viewHolder.itemView.etRestaurantNameRes.text.toString(),link,viewHolder.itemView.etCostRes.text.toString().toInt(),viewHolder.itemView.etStartTimimgRes.text.toString(), viewHolder.itemView.etEndTimimgRes.text.toString(),aa)

                        mongoCollection?.insertOne(obj)?.getAsync{task ->
                            if(task.isSuccess)
                            {
                                Log.d("User", "Restaurant Uploaded Successfully")
                                val intent = Intent(viewHolder.itemView.context, MainActivity::class.java)
                                viewHolder.itemView.context.startActivity(intent)
                            }
                            else
                            {
                                Log.d("User", "Restaurant can not be uploaded ${task.error}")
                            }
                        }


                    } else {
                        Log.d("User", "did not delete a document.")
                    }
                } else {
                    Log.e("EXAMPLE", "failed to delete document with: ${task.error}")
                }
            }
        }


    }

    override fun getLayout(): Int {
        return R.layout.restaurant_adapter
    }

}
