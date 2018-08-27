package com.wolf.proxy;

import com.wolf.annotation.Transaction;
import com.wolf.annotation.Transactions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


interface HelloService {
    void update1();
    void update2();
}

class HelloServiceImpl implements HelloService {
    @Transaction(db = "user")
    @Transaction(db = "order")
    @Override
    public void update1() {
        System.out.println("update 1...");
        this.update2();
    }

    @Transaction(db = "order")
    @Override
    public void update2() {
        System.out.println("update 2...");
    }
}

class TestProxy {
    static HelloService getProxy() {
        HelloService service = new HelloServiceImpl();

        InvocationHandler handler = (proxy, method, parameters) -> {
            Method implMethod = service.getClass().getMethod(method.getName(), method.getParameterTypes());
            List<Transaction> transactionList = getTransactions(implMethod);
            for (Transaction transaction : transactionList) {
                System.out.println("open transaction, db:" + transaction.db());
            }
            Object rval = method.invoke(service, parameters);
            for (Transaction transaction : transactionList) {
                System.out.println("commit transaction, db:" + transaction.db());
            }
            return rval;
        };

        return (HelloService) Proxy.newProxyInstance(TestProxy.class.getClassLoader(),
                new Class[]{HelloService.class}, handler);
    }

    private static List<Transaction> getTransactions(Method implMethod) {
        List<Transaction> transactionList = new LinkedList<>();
        Transactions ts = implMethod.getAnnotation(Transactions.class);
        if (ts != null) {
            transactionList.addAll(Arrays.asList(ts.value()));
        }else{
            Transaction t = implMethod.getAnnotation(Transaction.class);
            if (t != null) {
                transactionList.add(t);
            }
        }
        return transactionList;
    }
}


class Main{
    public static void main(String[] args) {
        HelloService serviceProxy = TestProxy.getProxy();
        serviceProxy.update1();
    }
}