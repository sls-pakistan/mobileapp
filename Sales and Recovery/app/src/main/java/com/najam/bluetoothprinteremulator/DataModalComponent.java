package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.content.SharedPreferences;

import com.najam.bluetoothprinteremulator.general.Utility;
import com.najam.bluetoothprinteremulator.modal.Account;
import com.najam.bluetoothprinteremulator.modal.BaseModal;
import com.najam.bluetoothprinteremulator.modal.Category;
import com.najam.bluetoothprinteremulator.modal.Company;
import com.najam.bluetoothprinteremulator.modal.Defaults;
import com.najam.bluetoothprinteremulator.modal.Invoice;
import com.najam.bluetoothprinteremulator.modal.InvoiceItem;
import com.najam.bluetoothprinteremulator.modal.Product;
import com.najam.bluetoothprinteremulator.modal.SubZone;
import com.najam.bluetoothprinteremulator.modal.Transaction;
import com.najam.bluetoothprinteremulator.modal.Zone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by najam on 01-04-2017.
 */

public class DataModalComponent {

    public static String data;
    private static DataModalComponent instance;
    private static BaseModal baseModal;
    private DateFormat dateFormatMDY = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
    private DateFormat dateFormatMDDY = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a", Locale.ENGLISH);
    //"14-Aug-17 12:00:00 AM"
    private String assetFilePath;
    private int maxAccId;
    private int maxInvId;
    private int maxTrId;
    private Invoice invoiceInProgress;

    private DataModalComponent() {
    }

    private Date getDateFromString(String str){
        Date date = null;
        try {
            if (str.matches("^\\d{2}\\-((Jan)|(Feb)|(Mar)|(Apr)|(May)|(Jun)|(Jul)|(Aug)|(Sep)|(Oct)|(Nov)|(Dec))\\-\\d{2} \\d{2}:\\d{2}:\\d{2} ((AM)|(PM))$")){
                date = dateFormatMDDY.parse(str);
            }else{
                date = dateFormatMDY.parse(str);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
        return date;
    }
    public static DataModalComponent getInstance(Context context) throws IOException, JSONException {
        if (instance == null) {
            instance = new DataModalComponent();
            data = instance.loadJSONFromAsset(context);
            if (data != null) {
                System.out.print("Starting Reading JSON");
                JSONObject jo = new JSONObject(data);
                System.out.print("Starting Loading JSON");
                baseModal = instance.loadBaseModalFromJSON(jo);
            }
        } else {
            if (baseModal == null) {
                data = instance.loadJSONFromAsset(context);
                if (data != null) {
                    baseModal = instance.loadBaseModalFromJSON(new JSONObject(data));
                }
            }
        }
        return instance;
    }

    public Invoice getInvoiceById(String id) {
        if (baseModal != null) {
            for (int i = 0; i < baseModal.getNewInvoices().size(); i++) {
                if (baseModal.getNewInvoices().get(i).getSaleId().equalsIgnoreCase(id)) {
                    return baseModal.getNewInvoices().get(i);
                }
            }
        }
        return null;
    }

    public Account getAccountByAccountID(String accId) {
        if (baseModal != null) {
            if (baseModal.getDefaults().getCashAccId().equals(accId)){
                Account ca = new Account();
                ca.setAccID(accId);
                ca.setAccName("Cash In Hand");
                ca.setBalance(0.0);
                ca.setsZoneID("");
                ca.setZoneID("");
                return ca ;
            }
            for (int i = 0; i < baseModal.getZones().size(); i++) {
                for (int j = 0; j < baseModal.getZones().get(i).getSubZonses().size(); j++) {
                    for (int k = 0; k < baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().size(); k++) {
                        if (baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).getAccID().equalsIgnoreCase(accId) ||
                                baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).getAccName().equalsIgnoreCase(accId)) {
                            return baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k);
                        }
                    }

                }
            }


            for (int i = 0; i < baseModal.getNewAccounts().size(); i++) {
                if (baseModal.getNewAccounts().get(i).getAccID().equalsIgnoreCase(accId) ||
                        baseModal.getNewAccounts().get(i).getAccName().equalsIgnoreCase(accId)) {
                    return baseModal.getNewAccounts().get(i);
                }
            }
        }
        return null;
    }

    public int getMaxAccId() {
        return maxAccId;
    }

    public void setMaxAccId(int maxAccId) {
        this.maxAccId = maxAccId;
    }

    String getMaxInvId() {
        ArrayList<Invoice> invoices = new ArrayList<>();
        if (baseModal != null) {
            invoices = baseModal.getNewInvoices();
        }
        return String.valueOf(invoices.size());
    }


    private List<Invoice> getNewInvoicesByAcc(String accId) {
        List<Invoice> invoices = new ArrayList<>();
        if (baseModal != null) {
            for (int l = 0; l < baseModal.getNewInvoices().size(); l++) {
                if (baseModal.getNewInvoices().get(l).getAccId().equals(accId)) {
                    invoices.add(baseModal.getNewInvoices().get(l));
                }
            }
        }
        return invoices;
    }


    public int getMaxTrId() {
        return maxTrId++;
    }

    public BaseModal getBaseModal() {
        return baseModal;
    }


    public void setBaseModal(BaseModal baseModal) {
        DataModalComponent.baseModal = baseModal;
    }


    public ArrayList<SubZone> getSubZoneListByZoneId(String zoneId) {
        for (int i = 0; i < baseModal.getZones().size(); i++) {
            if (baseModal.getZones().get(i).getZoneID().equals(zoneId)) {
                return baseModal.getZones().get(i).getSubZonses();
            }
        }
        return null;
    }


    public ArrayList<Account> getAccountsBySubZoneId(String zoneId, String subZoneId) {
        for (int i = 0; i < baseModal.getZones().size(); i++) {
            if (baseModal.getZones().get(i).getZoneID().equals(zoneId)) {
                for (int j = 0; j < baseModal.getZones().get(i).getSubZonses().size(); j++) {
                    if (baseModal.getZones().get(i).getSubZonses().get(j).getsZoneID().equals(subZoneId)) {
                        return baseModal.getZones().get(i).getSubZonses().get(j).getAccounts();
                    }
                }
            }
        }
        return null;
    }

    Account getAccountByAccId(String zoneId, String subZoneId, String accId) {
        if (baseModal != null) {
            for (int i = 0; i < baseModal.getZones().size(); i++) {
                if (baseModal.getZones().get(i).getZoneID().equals(zoneId)) {
                    for (int j = 0; j < baseModal.getZones().get(i).getSubZonses().size(); j++) {
                        if (baseModal.getZones().get(i).getSubZonses().get(j).getsZoneID().equals(subZoneId)) {
                            for (int k = 0; k < baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().size(); k++) {
                                if (baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).getAccID().equals(accId)) {
                                    return baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    String getZoneId(String zoneName) {
        if (baseModal != null) {
            String zId = "";
            for (int i = 0; i < baseModal.getZones().size(); i++) {
                if (baseModal.getZones().get(i).getZoneName().equals(zoneName)) {
                    zId = baseModal.getZones().get(i).getZoneID();
                    break;
                }
            }
            return zId;
        } else {
            return "";
        }
    }

    String getSubZoneId(String subZoneName) {
        for (int i = 0; i < baseModal.getZones().size(); i++) {
            for (int j = 0; j < baseModal.getZones().get(i).getSubZonses().size(); j++) {
                if (baseModal.getZones().get(i).getSubZonses().get(j).getsZoneName().equals(subZoneName)) {
                    return baseModal.getZones().get(i).getSubZonses().get(j).getsZoneID();
                }
            }
        }
        return "";
    }

    String getAccountId(String accName) {
        if (baseModal != null) {
            for (int i = 0; i < baseModal.getZones().size(); i++) {
                for (int j = 0; j < baseModal.getZones().get(i).getSubZonses().size(); j++) {
                    for (int k = 0; k < baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().size(); k++) {
                        if (baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).getAccName().equals(accName)) {
                            return baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).getAccID();
                        }
                    }
                }
            }
        }
        return "";
    }

    Account getAccountByAccName(String zoneName, String subZoneName, String accName) {
        if (baseModal != null) {
            for (int i = 0; i < baseModal.getZones().size(); i++) {
                if (baseModal.getZones().get(i).getZoneName().equals(zoneName)) {
                    for (int j = 0; j < baseModal.getZones().get(i).getSubZonses().size(); j++) {
                        if (baseModal.getZones().get(i).getSubZonses().get(j).getsZoneName().equals(subZoneName)) {
                            for (int k = 0; k < baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().size(); k++) {
                                if (baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).getAccName().equals(accName)) {
                                    return baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (baseModal != null) {
            List<Account> accounts = baseModal.getNewAccounts();
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getAccName().equals(accName)
                        && accounts.get(i).getZoneID().equals(zoneName)
                        && accounts.get(i).getsZoneID().equals(subZoneName)) {
                    return accounts.get(i);
                }
            }
        }
        return null;
    }

    public ArrayList<Product> getProductsByCatId(String catId) {
        for (int i = 0; i < baseModal.getInventory().getCategories().size(); i++) {
            if (baseModal.getInventory().getCategories().get(i).getCatID().equals(catId)) {
                return baseModal.getInventory().getCategories().get(i).getProducts();
            }
        }
        return null;
    }


    public String loadJSONFromAsset(Context c) throws IOException {
        SharedPreferences prefs = c.getSharedPreferences("AddressPref", MODE_PRIVATE);
        assetFilePath = prefs.getString("Address", null);
        String jString = null;
        if (assetFilePath != null) {
            File yourFile = new File(assetFilePath);
            if(yourFile.exists()) {
                FileInputStream stream = new FileInputStream(yourFile);
                try {
                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    System.gc();
                    jString = Charset.defaultCharset().decode(bb).toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    stream.close();
                }
            }
        }
        return jString;
    }

    private BaseModal loadBaseModalFromJSON(JSONObject data) throws JSONException {
        BaseModal modal = new BaseModal();

        JSONObject jo = data.getJSONObject("defaults");
        Defaults defaults = modal.getDefaults();
        defaults.setCashAccId(jo.getString("cashAccId"));

        Company company = defaults.getCompany();
        jo = jo.getJSONObject("company");
        company.setCmpId(jo.getString("cmpId"));
        company.setName(jo.getString("name"));
        company.setAddress(jo.getString("address"));
        company.setPhone(jo.getString("phone"));

        jo = data.getJSONObject("inventory");
        JSONArray ja = jo.getJSONArray("categories");

        ArrayList<Category> categories = modal.getInventory().getCategories();
        Category category;
        ArrayList<Product> products;
        Product product;

        for (int i = 0; i < ja.length(); i++) {
            category = new Category();
            category.setCatID(ja.getJSONObject(i).getString("catID"));
            category.setCatName(ja.getJSONObject(i).getString("catName"));
            JSONArray jaProd = ja.getJSONObject(i).getJSONArray("products");
            products = category.getProducts();
            for (int p = 0; p < jaProd.length(); p++) {
                product = new Product();
                product.setCatID(jaProd.getJSONObject(p).getString("catID"));
                product.setProID(jaProd.getJSONObject(p).getString("proID"));
                product.setProName(jaProd.getJSONObject(p).getString("proName"));
                product.setProPrice(jaProd.getJSONObject(p).getDouble("proPrice"));
                product.setUnit(jaProd.getJSONObject(p).getString("unit"));
                products.add(product);
            }
            categories.add(category);
        }

        ja = data.getJSONArray("zones");

        ArrayList<Zone> zones = modal.getZones();
        Zone zone;

        ArrayList<SubZone> subZones;
        SubZone subZone;

        ArrayList<Account> accounts;
        Account account;

        ArrayList<Transaction> transactions;
        Transaction transaction;

        ArrayList<Invoice> invoices;
        Invoice invoice;

        ArrayList<InvoiceItem> items;
        InvoiceItem item;

        for (int i = 0; i < ja.length(); i++) {
            zone = new Zone();
            zone.setZoneID(ja.getJSONObject(i).getString("zoneID"));
            zone.setZoneName(ja.getJSONObject(i).getString("zoneName"));
            JSONArray jaSubZones = ja.getJSONObject(i).getJSONArray("subZones");
            subZones = zone.getSubZonses();
            for (int s = 0; s < jaSubZones.length(); s++) {
                subZone = new SubZone();
                subZone.setZoneID(jaSubZones.getJSONObject(s).getString("zoneID"));
                subZone.setsZoneID(jaSubZones.getJSONObject(s).getString("sZoneID"));
                subZone.setsZoneName(jaSubZones.getJSONObject(s).getString("sZoneName"));
                JSONArray jaAccounts = jaSubZones.getJSONObject(s).getJSONArray("accounts");
                accounts = subZone.getAccounts();
                for (int a = 0; a < jaAccounts.length(); a++) {
                    account = new Account();
                    account.setsZoneID(jaAccounts.getJSONObject(a).getString("sZoneID"));
                    account.setAccID(jaAccounts.getJSONObject(a).getString("accID"));
                    account.setAccName(jaAccounts.getJSONObject(a).getString("accName"));
                    account.setAddress(jaAccounts.getJSONObject(a).getString("address"));
                    account.setPhone(jaAccounts.getJSONObject(a).getString("phone"));
                    account.setBalance(jaAccounts.getJSONObject(a).getDouble("balance"));
                    account.setRank(jaAccounts.getJSONObject(a).getInt("rank"));


                    JSONArray jaTransactions = jaAccounts.getJSONObject(a).getJSONArray("transactions");
                    transactions = account.getTransactions();

                    for (int t = 0; t < jaTransactions.length(); t++) {
                        transaction = new Transaction();
                        String string = jaTransactions.getJSONObject(t).getString("date");
                        Date date = getDateFromString(string);
                        transaction.setDate(date);

                        transaction.setVouTypeID(jaTransactions.getJSONObject(t).getString("vouTypeID"));
                        transaction.setVouID(jaTransactions.getJSONObject(t).getString("vouID"));
                        if (jaTransactions.getJSONObject(t).has("vouDesc"))
                            transaction.setVouDesc(jaTransactions.getJSONObject(t).getString("vouDesc"));
                        if (jaTransactions.getJSONObject(t).has("accId"))
                            transaction.setAccId(jaTransactions.getJSONObject(t).getString("accId"));
                        if (jaTransactions.getJSONObject(t).has("debit"))
                            transaction.setDebit(jaTransactions.getJSONObject(t).getDouble("debit"));
                        if (jaTransactions.getJSONObject(t).has("credit"))
                            transaction.setCredit(jaTransactions.getJSONObject(t).getDouble("credit"));
                        transactions.add(transaction);
                    }
                    JSONArray jaInvoices = jaAccounts.getJSONObject(i).getJSONArray("invoices");
                    invoices = account.getInvoices();

                    for (int in = 0; in < jaInvoices.length(); in++) {
                        invoice = new Invoice();
                        invoice.setSaleId(jaInvoices.getJSONObject(in).getString("saleID"));
                        String string = jaInvoices.getJSONObject(in).getString("saleDate");
                        invoice.setSaleDate(getDateFromString(string));
                        if (jaInvoices.getJSONObject(in).has("saleDesc")) {
                            invoice.setSaleDesc(jaInvoices.getJSONObject(in).getString("saleDesc"));
                        }
                        invoice.setAccId(jaInvoices.getJSONObject(in).getString("accId"));
                        if (jaInvoices.getJSONObject(in).has("discRate")) {
                            invoice.setDiscRate(jaInvoices.getJSONObject(in).getDouble("discRate"));
                        }
                        if (jaInvoices.getJSONObject(in).has("bonusRate")) {
                            invoice.setBonusRate(jaInvoices.getJSONObject(in).getDouble("bonusRate"));
                        }
                        if (jaInvoices.getJSONObject(in).has("cashPaid")) {
                            invoice.setCashPaid(jaInvoices.getJSONObject(in).getDouble("cashPaid"));
                        }
                        JSONArray jaInvoiceItems = jaInvoices.getJSONObject(in).getJSONArray("items");
                        items = invoice.getItems();
                        for (int ii = 0; ii < jaInvoiceItems.length(); ii++) {
                            if(jaInvoiceItems.getJSONObject(ii).has("saleID")) {
                                item = new InvoiceItem();
                                item.setSaleID(jaInvoiceItems.getJSONObject(ii).getString("saleID"));
                                item.setProID(jaInvoiceItems.getJSONObject(ii).getString("proID"));
                                if (jaInvoiceItems.getJSONObject(ii).has("proDesc")) {
                                    item.setProDesc(jaInvoiceItems.getJSONObject(ii).getString("proDesc"));
                                }
                                if (jaInvoiceItems.getJSONObject(ii).has("salDetDesc"))
                                    item.setSalDetDesc(jaInvoiceItems.getJSONObject(ii).getString("salDetDesc"));
                                item.setProPrice(jaInvoiceItems.getJSONObject(ii).getDouble("proPrice"));

                                if (jaInvoiceItems.getJSONObject(ii).has("priceDisc")) {
                                    item.setPriceDisc(jaInvoiceItems.getJSONObject(ii).getDouble("priceDisc"));
                                }
                                item.setProQty(jaInvoiceItems.getJSONObject(ii).getDouble("proQty"));
                                items.add(item);
                            }
                        }
                        invoices.add(invoice);
                    }
                    accounts.add(account);
                }
                subZones.add(subZone);
            }
            zones.add(zone);
        }

        if (data.has("newAccounts")) {
            JSONArray newAccountsArray = data.getJSONArray("newAccounts");
            for (int i = 0; i < newAccountsArray.length(); i++) {
                JSONObject newAccountObj = newAccountsArray.getJSONObject(i);
                Account newAccount = new Account();
                newAccount.setsZoneID(newAccountObj.getString("sZoneID"));
                newAccount.setZoneID(newAccountObj.getString("zoneID"));
                newAccount.setAccID(newAccountObj.getString("accID"));
                newAccount.setAccName(newAccountObj.getString("accName"));
                if(newAccountObj.has("address"))
                    newAccount.setAddress(newAccountObj.getString("address"));
                if(newAccountObj.has("phone"))
                    newAccount.setPhone(newAccountObj.getString("phone"));

                if(newAccountObj.has("balance"))
                    newAccount.setBalance(newAccountObj.getDouble("balance"));
                if(newAccountObj.has("rank"))
                    newAccount.setRank(newAccountObj.getInt("rank"));
                JSONArray transactionArray = newAccountObj.getJSONArray("transactions");
                for (int j = 0; j < transactionArray.length(); j++) {
                    JSONObject transactionObj = transactionArray.getJSONObject(j);
                    transaction = new Transaction();
                    String string = transactionObj.getString("date");
                    transaction.setDate(getDateFromString(string));

                    transaction.setVouTypeID(transactionObj.getString("vouTypeID"));
                    if (transactionObj.has("vouID")) {
                        transaction.setVouID(transactionObj.getString("vouID"));
                    } else {
                        transaction.setVouID(" ");
                    }
                    if (transactionObj.has("vouDesc"))
                        transaction.setVouDesc(transactionObj.getString("vouDesc"));
                    if (transactionObj.has("accId"))
                        transaction.setAccId(transactionObj.getString("accId"));
                    if (transactionObj.has("debit"))
                        transaction.setDebit(transactionObj.getDouble("debit"));
                    if (transactionObj.has("credit"))
                        transaction.setCredit(transactionObj.getDouble("credit"));
                    newAccount.getTransactions().add(transaction);
                }
                modal.getNewAccounts().add(newAccount);
            }
        }

        if (data.has("newReceipts")) {
            JSONArray newTransactionsArray = data.getJSONArray("newReceipts");
            for (int i = 0; i < newTransactionsArray.length(); i++) {
                JSONObject newTransactionObj = newTransactionsArray.getJSONObject(i);
                Transaction newTransaction = new Transaction();

                String string = newTransactionObj.getString("date");
                newTransaction.setDate(getDateFromString(string));
                newTransaction.setVouTypeID(newTransactionObj.getString("vouTypeID"));
                if (newTransactionObj.has("vouID")) {
                    newTransaction.setVouID(newTransactionObj.getString("vouID"));
                } else {
                    newTransaction.setVouID("");
                }
                if(newTransactionObj.has("vouDesc"))
                    newTransaction.setVouDesc(newTransactionObj.getString("vouDesc"));
                newTransaction.setAccId(newTransactionObj.getString("accId"));
                if(newTransactionObj.has("debit"))
                    newTransaction.setDebit(newTransactionObj.getDouble("debit"));
                if(newTransactionObj.has("credit"))
                    newTransaction.setCredit(newTransactionObj.getDouble("credit"));
                modal.getNewReceipts().add(newTransaction);
            }
        }
        if (data.has("newInvoices")) {
            JSONArray newInvoicesArray = data.getJSONArray("newInvoices");
            for (int i = 0; i < newInvoicesArray.length(); i++) {
                JSONObject newInvoiceObj = newInvoicesArray.getJSONObject(i);
                Invoice newInvoice = new Invoice();

                String string = newInvoiceObj.getString("saleDate");
                newInvoice.setSaleDate(getDateFromString(string));

                if (newInvoiceObj.has("saleDesc")) {
                    newInvoice.setSaleDesc(newInvoiceObj.getString("saleDesc"));
                } else {
                    newInvoice.setSaleDesc("");
                }
                newInvoice.setAccId(newInvoiceObj.getString("accId"));
                newInvoice.setSaleId(newInvoiceObj.getString("saleID"));
                if(newInvoiceObj.has("discRate"))
                    newInvoice.setDiscRate(newInvoiceObj.getDouble("discRate"));
                if(newInvoiceObj.has("bonusRate"))
                    newInvoice.setBonusRate(newInvoiceObj.getDouble("bonusRate"));
                if(newInvoiceObj.has("cashPaid"))
                    newInvoice.setCashPaid(newInvoiceObj.getDouble("cashPaid"));
                JSONArray newInvoiceItemsArray = newInvoiceObj.getJSONArray("items");
                for (int j = 0; j < newInvoiceItemsArray.length(); j++) {
                    JSONObject itemObj = newInvoiceItemsArray.getJSONObject(j);
                    if(itemObj.has("saleID")) {
                        item = new InvoiceItem();
                        item.setSaleID(itemObj.getString("saleID"));
                        item.setProID(itemObj.getString("proID"));
                        if (itemObj.has("proDesc"))
                            item.setProDesc(itemObj.getString("proDesc"));
                        if (itemObj.has("salDetDesc")) {
                            item.setSalDetDesc(itemObj.getString("salDetDesc"));
                        } else {
                            item.setSalDetDesc("");
                        }
                        item.setProPrice(itemObj.getDouble("proPrice"));
                        if (itemObj.has("priceDisc"))
                            item.setPriceDisc(itemObj.getDouble("priceDisc"));
                        item.setProQty(itemObj.getDouble("proQty"));
                        newInvoice.getItems().add(item);
                    }
                }
                modal.getNewInvoices().add(newInvoice);
            }
        }
        return modal;
    }

    Invoice getInvoiceInProgress() {
        if (invoiceInProgress == null) {
            this.invoiceInProgress = new Invoice();
        }
        return this.invoiceInProgress;
    }

    void setInvoiceInProgress(Invoice invoice) {
        this.invoiceInProgress = invoice;
    }

    void saveModalToFile() {
        try {
            JSONObject rootObj = new JSONObject();
            JSONObject defaultsObj = new JSONObject();
            defaultsObj.put("cashAccId", baseModal.getDefaults().getCashAccId());
            JSONObject companyObj = new JSONObject();
            companyObj.put("cmpId", baseModal.getDefaults().getCompany().getCmpId());
            companyObj.put("name", baseModal.getDefaults().getCompany().getName());
            companyObj.put("address", baseModal.getDefaults().getCompany().getAddress());
            companyObj.put("phone", baseModal.getDefaults().getCompany().getPhone());
            defaultsObj.put("company", companyObj);
            rootObj.put("defaults", defaultsObj);

            JSONArray categoriesArray = new JSONArray();
            JSONObject inventoryObj = new JSONObject();
            int catCounter = 0;
            for (Category category : baseModal.getInventory().getCategories()) {
                JSONObject categoryObj = new JSONObject();
                categoryObj.put("catID", category.getCatID());
                categoryObj.put("catName", category.getCatName());
                JSONArray productArray = new JSONArray();
                int prodCounter = 0;
                for (Product product : category.getProducts()) {
                    JSONObject productObj = new JSONObject();
                    productObj.put("catID", product.getCatID());
                    productObj.put("proID", product.getProID());
                    productObj.put("proName", product.getProName());
                    productObj.put("proPrice", product.getProPrice());
                    productObj.put("unit", product.getUnit());
                    productArray.put(prodCounter++, productObj);
                }
                categoryObj.put("products", productArray);
                categoriesArray.put(catCounter++, categoryObj);
            }
            inventoryObj.put("categories", categoriesArray);
            rootObj.put("inventory", inventoryObj);

            JSONArray zonesArray = new JSONArray();
            int zoneCounter = 0;
            for (Zone zone : baseModal.getZones()) {
                JSONObject zoneObj = new JSONObject();
                zoneObj.put("zoneID", zone.getZoneID());
                zoneObj.put("zoneName", zone.getZoneName());
                JSONArray subZoneArray = new JSONArray();
                int subZoneCounter = 0;
                for (SubZone subZone : zone.getSubZonses()) {
                    JSONObject subZoneObj = new JSONObject();
                    subZoneObj.put("zoneID", subZone.getZoneID());
                    subZoneObj.put("sZoneID", subZone.getsZoneID());
                    subZoneObj.put("sZoneName", subZone.getsZoneName());

                    JSONArray accountsArray = new JSONArray();
                    int accountCounter = 0;
                    for (Account account : subZone.getAccounts()) {
                        JSONObject accountObj = new JSONObject();
                        accountObj.put("sZoneID", account.getsZoneID());
                        accountObj.put("accID", account.getAccID());
                        accountObj.put("accName", account.getAccName());
                        accountObj.put("address", account.getAddress());
                        accountObj.put("phone", account.getPhone());
                        accountObj.put("balance", account.getBalance());
                        accountObj.put("rank", account.getRank());
                        JSONArray transactionsArray = new JSONArray();
                        int transactionCounter = 0;
                        for (Transaction transaction : account.getTransactions()) {
                            JSONObject transactionObj = new JSONObject();
                            transactionObj.put("date", dateFormatMDY.format(transaction.getDate()));
                            transactionObj.put("vouTypeID", transaction.getVouTypeID());
                            transactionObj.put("vouID", transaction.getVouID());
                            transactionObj.put("vouDesc", transaction.getVouDesc());
                            transactionObj.put("accId", transaction.getAccId());
                            transactionObj.put("debit", transaction.getDebit());
                            transactionObj.put("credit", transaction.getCredit());
                            transactionsArray.put(transactionCounter++, transactionObj);
                        }
                        accountObj.put("transactions", transactionsArray);

                        JSONArray invoicesArray = new JSONArray();
                        int invoiceCounter = 0;

                        for (Invoice invoice : account.getInvoices()) {
                            JSONObject invoiceObj = new JSONObject();
                            invoiceObj.put("saleID", invoice.getSaleId());
                            invoiceObj.put("saleDate", dateFormatMDY.format(invoice.getSaleDate()));
                            invoiceObj.put("saleDesc", invoice.getSaleDesc());
                            invoiceObj.put("accId", invoice.getAccId());
                            invoiceObj.put("discRate", invoice.getDiscRate());
                            invoiceObj.put("bonusRate", invoice.getBonusRate());
                            invoiceObj.put("cashPaid", invoice.getCashPaid());
                            JSONArray invoiceItemsArray = new JSONArray();
                            int invoiceItemCounter = 0;
                            for (InvoiceItem invoiceItem : invoice.getItems()) {
                                JSONObject invoiceItemObj = new JSONObject();
                                invoiceItemObj.put("saleID", invoiceItem.getSaleID());
                                invoiceItemObj.put("proID", invoiceItem.getProID());
                                invoiceItemObj.put("proDesc", invoiceItem.getProDesc());
                                //invoiceItemObj.put("salDetDesc", invoiceItem.get);
                                invoiceItemObj.put("proPrice", invoiceItem.getProPrice());
                                invoiceItemObj.put("priceDisc", invoiceItem.getPriceDisc());
                                invoiceItemObj.put("proQty", invoiceItem.getProQty());
                                invoiceItemsArray.put(invoiceItemCounter++, invoiceItemObj);
                            }
                            invoiceObj.put("items", invoiceItemsArray);
                            invoicesArray.put(invoiceCounter++, invoiceObj);
                        }
                        accountObj.put("invoices", invoicesArray);
                        accountsArray.put(accountCounter++, accountObj);
                    }
                    subZoneObj.put("accounts", accountsArray);
                    subZoneArray.put(subZoneCounter++, subZoneObj);
                }
                zoneObj.put("subZones", subZoneArray);
                zonesArray.put(zoneCounter++, zoneObj);
            }
            rootObj.put("zones", zonesArray);

            JSONArray newAccountsArray = new JSONArray();
            int newAccountCounter = 0;
            for (Account newAccount : baseModal.getNewAccounts()) {
                JSONObject newAccountObj = new JSONObject();
                newAccountObj.put("sZoneID", newAccount.getsZoneID());
                newAccountObj.put("zoneID", newAccount.getZoneID());
                newAccountObj.put("accID", newAccount.getAccID());
                newAccountObj.put("accName", newAccount.getAccName());
                newAccountObj.put("address", newAccount.getAddress());
                newAccountObj.put("phone", newAccount.getPhone());
                newAccountObj.put("balance", newAccount.getBalance());
                newAccountObj.put("rank", newAccount.getRank());
                int transactionCounter = 0;
                JSONArray transactionsArray = new JSONArray();
                for (Transaction transaction : newAccount.getTransactions()) {
                    JSONObject transactionObj = new JSONObject();
                    transactionObj.put("date", dateFormatMDY.format(transaction.getDate()));
                    transactionObj.put("vouTypeID", transaction.getVouTypeID());
                    transactionObj.put("vouID", transaction.getVouID());
                    transactionObj.put("vouDesc", transaction.getVouDesc());
                    transactionObj.put("accId", transaction.getAccId());
                    transactionObj.put("debit", transaction.getDebit());
                    transactionObj.put("credit", transaction.getCredit());
                    transactionsArray.put(transactionCounter++, transactionObj);
                }
                newAccountObj.put("transactions", transactionsArray);
                newAccountsArray.put(newAccountCounter++, newAccountObj);
            }
            rootObj.put("newAccounts", newAccountsArray);


            JSONArray newTransactionsArray = new JSONArray();
            int newTransactionCounter = 0;
            for (Transaction newTransaction : baseModal.getNewReceipts()) {
                JSONObject newTransactionObj = new JSONObject();
                newTransactionObj.put("date", dateFormatMDY.format(newTransaction.getDate()));
                newTransactionObj.put("vouTypeID", newTransaction.getVouTypeID());
                if (newTransaction.getVouID() != null) {
                    newTransactionObj.put("vouID", newTransaction.getVouID());
                } else {
                    newTransactionObj.put("vouID", "");

                }
                newTransactionObj.put("vouDesc", newTransaction.getVouDesc());
                newTransactionObj.put("accId", newTransaction.getAccId());
                newTransactionObj.put("debit", newTransaction.getDebit());
                newTransactionObj.put("credit", newTransaction.getCredit());
                newTransactionsArray.put(newTransactionCounter++, newTransactionObj);
            }
            rootObj.put("newReceipts", newTransactionsArray);

            JSONArray newInvoicesArray = new JSONArray();
            int newInvoiceCounter = 0;
            for (Invoice newInvoice : baseModal.getNewInvoices()) {
                JSONObject newInvoiceObj = new JSONObject();
                newInvoiceObj.put("saleID", newInvoice.getSaleId());
                newInvoiceObj.put("saleDate", dateFormatMDY.format(newInvoice.getSaleDate()));
                newInvoiceObj.put("saleDesc", newInvoice.getSaleDesc());
                newInvoiceObj.put("accId", newInvoice.getAccId());
                newInvoiceObj.put("discRate", newInvoice.getDiscRate());
                if (newInvoice.getBonusRate() != null) {
                    newInvoiceObj.put("bonusRate", newInvoice.getBonusRate());
                } else {
                    newInvoiceObj.put("bonusRate", 0);

                }
                newInvoiceObj.put("cashPaid", newInvoice.getCashPaid());
                JSONArray invoiceItemsArray = new JSONArray();
                int invoiceItemCounter = 0;
                for (InvoiceItem invoiceItem : newInvoice.getItems()) {
                    JSONObject invoiceItemObj = new JSONObject();
                    invoiceItemObj.put("saleID", invoiceItem.getSaleID());
                    invoiceItemObj.put("proID", invoiceItem.getProID());
                    invoiceItemObj.put("proDesc", invoiceItem.getProDesc());
                    //invoiceItemObj.put("salDetDesc", invoiceItem.get);
                    invoiceItemObj.put("proPrice", invoiceItem.getProPrice());
                    invoiceItemObj.put("priceDisc", invoiceItem.getPriceDisc());
                    invoiceItemObj.put("proQty", invoiceItem.getProQty());
                    invoiceItemsArray.put(invoiceItemCounter++, invoiceItemObj);
                }
                newInvoiceObj.put("items", invoiceItemsArray);
                newInvoicesArray.put(newInvoiceCounter++, newInvoiceObj);
            }
            rootObj.put("newInvoices", newInvoicesArray);


            try {
                File file = new File(assetFilePath);
                File folder = file.getParentFile();
                if(file.exists()){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                    String newname = simpleDateFormat.format(new Date());
                    File newfile = new File(folder.getPath() + "//" + newname + ".json");
                    FileOutputStream fosbackup = new FileOutputStream(newfile);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fosbackup);

                    try {
                        outputStreamWriter.write(rootObj.toString()) ;
                    }catch(IOException e){
                        e.printStackTrace();
                    }finally {
                        outputStreamWriter.close();
                        fosbackup.close();
                    }
                }
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
                outputStreamWriter.write(rootObj.toString());
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getDataToUpload(Context context) {
        String jsonString = "";
        if (data != null) {
            try {
                data = loadJSONFromAsset(context);
                JSONObject jsonObject = new JSONObject(data);
                JSONArray newAccounts = jsonObject.getJSONArray("newAccounts");
                JSONArray newReceipts = jsonObject.getJSONArray("newReceipts");
                JSONArray newInvoices = jsonObject.getJSONArray("newInvoices");
                jsonString = "{" + "\"newAccount\":" + newAccounts.toString() + ","
                        + "\"newReceipts\":" + newReceipts.toString() + ","
                        + "\"newInvoices\":" + newInvoices.toString() + "}";
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonString;
    }

    void updateTransactions(String accountId, String zone, String subZone, Transaction transactions) {
        boolean isFound = false;
        for (int i = 0; i < baseModal.getZones().size(); i++) {
            if (baseModal.getZones().get(i).getZoneID().equals(zone)) {
                for (int j = 0; j < baseModal.getZones().get(i).getSubZonses().size(); j++) {
                    if (baseModal.getZones().get(i).getSubZonses().get(j).getsZoneID().equals(subZone)) {
                        for (int k = 0; k < baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().size(); k++) {
                            if (baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).getAccID().equals(accountId)) {
                                baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).getTransactions().add(transactions);
                                isFound = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (!isFound) {
            //TODO: failure
        }
    }

    void updateAccountBalance(String accountName, String zone, String subZone, String newBalance) {
        boolean isFound = false;
        for (int i = 0; i < baseModal.getZones().size(); i++) {
            if (baseModal.getZones().get(i).getZoneName().equals(zone)) {
                for (int j = 0; j < baseModal.getZones().get(i).getSubZonses().size(); j++) {
                    if (baseModal.getZones().get(i).getSubZonses().get(j).getsZoneName().equals(subZone)) {
                        for (int k = 0; k < baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().size(); k++) {
                            if (baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).getAccName().equals(accountName)) {
                                isFound = true;
                                baseModal.getZones().get(i).getSubZonses().get(j).getAccounts().get(k).setBalance(Utility.getDouble(newBalance));
                            }
                        }
                    }
                }
            }
        }
        if (!isFound) {
            for (int i = 0; i < baseModal.getNewAccounts().size(); i++) {
                if (baseModal.getNewAccounts().get(i).getAccName().equals(accountName)) {
                    baseModal.getNewAccounts().get(i).setBalance(Utility.getDouble(newBalance));
                }
            }
        }
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {

            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
