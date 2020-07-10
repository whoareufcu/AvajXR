package com.jndv.avajxr.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.jndv.avajxr.R;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //订阅
//        observable.subscribe(observer);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        //指定事件发生的线程
//        Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
//        Schedulers.newThread():总是启用新线程，并在新线程执行操作
//        Schedulers.io():I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
//        Schedulers.computation():计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
//        AndroidSchedulers.mainThread():Android专用，它指定的操作将在 Android 主线程运行。

//        线程控制
//        subscribeOn():指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。
//        observeOn():指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriber.isUnsubscribed())
            subscriber.unsubscribe();
    }

    //观察者
    Observer<String> observer = new Observer<String>() {
        @Override
        public void onNext(String s) {
            Log.d(TAG, "Item: " + s);
        }

        //        事件队列完结。RxJava 不仅把每个事件单独处理，还会把它们看做一个队列。RxJava 规定，当不会再有新的 onNext() 发出时，需要触发 onCompleted() 方法作为标志。
        @Override
        public void onCompleted() {
            Log.d(TAG, "Completed!");
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "Error!");
        }
    };

    //观察者
    Subscriber<String> subscriber = new Subscriber<String>() {
        @Override
        public void onCompleted() {
            Log.e(TAG, "onCompleted ");
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String s) {
            Log.e(TAG, "onNext: " + s);
        }
    };

    //被观察者
//    Observable 即被观察者，它决定什么时候触发事件以及触发怎样的事件。
//    RxJava 使用 create() 方法来创建一个 Observable ，并为它定义事件触发规则：
    Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
        @Override
        public void call(Subscriber<? super String> subscriber) {
            subscriber.onNext("Hello");
            subscriber.onNext("Hi");
            subscriber.onNext("你好！");
            subscriber.onCompleted();
        }

    });
    //    RxJava 还提供了一些方法用来快捷创建事件队列，例如：
//    1.just(T...): 将传入的参数依次发送出来。
    Observable observable2 = Observable.just("Hello", "Hi", "你好！");

    //    2.from(T[]) / from(Iterable<? extends T>) : 将传入的数组或 Iterable 拆分成具体对象后，依次发送出来。
    String[] worlds = {"Hello", "你好", "Hi"};
    Observable observable3 = Observable.from(worlds);

//    上面 just(T...) 的例子和 from(T[]) 的例子，都和之前的 create(OnSubscribe) 的例子是等价的。


    //    除了 subscribe(Observer) 和 subscribe(Subscriber) ，subscribe() 还支持不完整定义的回调，RxJava 会自动根据定义创建出 Subscriber 。形式如下：
    Action1<String> onNextAction = new Action1<String>() {
        //onNext()
        @Override
        public void call(String s) {

        }
    };

    Action1<Throwable> onErrorAction = new Action1<Throwable>() {
        //onError()
        @Override
        public void call(Throwable throwable) {

        }
    };

    Action0 onCompletedAction = new Action0() {
        //onComplted()
        @Override
        public void call() {

        }
    };


    private void onActionSubscriber() {
        // 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
        observable.subscribe(onNextAction);
// 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
        observable.subscribe(onNextAction, onErrorAction);
// 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);


        String[] names = {"Bob", "Tom", "Albus"};
        Observable.from(names)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(TAG, "call: name is " + s);
                    }
                });
    }

    //变换
    private void rxJavaMap() {
        Observable.just("/xx/xx/123.png")
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        return null /*getBitmapFromPath(s)*/;
                    }
                })
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        /*showBitmap(bitmap);*/
                    }
                });


//        可以看到，map() 方法将参数中的 String 对象转换成一个 Bitmap 对象后返回，而在经过 map() 方法后，事件的参数类型也由 String 转为了 Bitmap。这种直接变换对象并返回的，是最常见的也最容易理解的变换
    }

    //    假设有一个数据结构『学生』，现在需要打印出一组学生的名字
    private void rxJavaMap2() {
        Student[] students = {};
        Observable.from(students)
                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student student) {
                        return student.getName();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(TAG, "call: 名字——" + s);
                    }
                });
    }

//    那么再假设：如果要打印出每个学生所需要修的所有课程的名称呢？（需求的区别在于，每个学生只有一个名字，但却有多个课程。）首先可以这样实现：
    private void rxJavaSimpleCourse(){
        Student[] students = {};
        Observable.from(students)
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        List<Course>courses=student.getCourses();
                        for (Course course:courses){
                            Log.e(TAG, "call: 课程名称——"+course.getName() );
                        }
                    }
                });
    }

//    依然很简单。那么如果我不想在 Subscriber 中使用 for 循环，而是希望 Subscriber 中直接传入单个的 Course 对象呢（这对于代码复用很重要）？
//    用 map() 显然是不行的，因为 map() 是一对一的转化，而我现在的要求是一对多的转化。那怎么才能把一个 Student 转化成多个 Course 呢？
//    这个时候，就需要用 flatMap() 了：
    private void rxJavaFlatMap() {
        Student[] students = {};
        Observable.from(students)
                .flatMap(new Func1<Student, Observable<Course>>() {
                    @Override
                    public Observable<Course> call(Student student) {
                        return Observable.from(student.getCourses());
                    }
                })
                .subscribe(new Action1<Course>() {
                    @Override
                    public void call(Course course) {
                        Log.e(TAG, "call: 课程名称——"+course.getName() );
                    }
                });
    }

//    从上面的代码可以看出， flatMap() 和 map() 有一个相同点：它也是把传入的参数转化之后返回另一个对象。但需要注意，和 map() 不同的是， flatMap() 中返回的是个 Observable
//    对象，并且这个 Observable 对象并不是被直接发送到了 Subscriber 的回调方法中。 flatMap() 的原理是这样的：
//            1. 使用传入的事件对象创建一个 Observable 对象；
//            2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
//            3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法。
//    这三个步骤，把事件拆成了两级，通过一组新创建的 Observable 将初始的对象『铺平』之后通过统一路径分发了下去。而这个『铺平』就是 flatMap() 所谓的 flat。

    class Student {
        private String name;
        private List<Course>courses;

        public void setCourses(List<Course> courses) {
            this.courses = courses;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class Course {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
