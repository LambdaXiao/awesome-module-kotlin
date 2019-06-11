# awesome-module-kotlin
Kotlin 编写的android 组件化开发项目demo

#### 一、组件化背景
  一个项目，随着业务的发展，模块会变的越来越多，代码量也会变的异常庞大，进而可能开发的人会越来越多，这种情况下如果还是基于单一工程架构，那就需要每一个开发者都熟悉所有的代码，而且代码之间耦合严重，一个模块穿插着大量其他业务模块的逻辑，严重的话可能使项目处于牵一发而动全身，不想轻易修改的局面；而且庞大的单一工程项目会导致编译速度极慢，开发者长时间等待编译结果，非常不利于开发工作。所以，就需要一个灵活的架构来解决这些问题，组件化架构思想应运而生。

![](/img/module1.png)

#### 二、整体架构

![](/img/module2.png)

- app：壳工程，用于将各个组件组装成一个完成app
- module_base：基础组件部分，不能独立运行，与业务无关，需要所有组件共同依赖的部分，如：网络请求封装、图片加载封装、ui相关基类、工具集合等（这些内容也可以依据分层原则放在不同的基础module中）
- ARouter：阿里开源的路由驱动组件，承载整个项目的路由工作
- module_main：业务组件1，如首页组件，可独立运行
- module_login：业务组件2，如登录组件，可独立运行
- module_me：业务组件3，如个人中心组件，可独立运行

#### 三、集成模式与组件模式的配置

首先，声明全局配置变量，来标识module的属性(app or library)

比如配置以下config.gradle文件
```
ext {
    android = [
            //true：组件模式，各组件可以单独运行；false：集成模式，只能运行或打包app module，也就是集成所有的module
            isModule                 : false,
            applicationId            : "app.android.module",
            compileSdkVersion        : 28,
            minSdkVersion            : 19,
            targetSdkVersion         : 28,
            versionCode              : 1,
            versionName              : "1.0.0",
            testInstrumentationRunner: "androidx.test.runner.AndroidJUnitRunner",
            multiDexEnabled          : true,
            flavorDimensions         : "versionCode",

            releaseDebuggable        : false,
            debugDebuggable          : true,
            releaseShrinkResources   : true,
            debugShrinkResources     : false,
            releaseMinifyEnabled     : true,
            debugMinifyEnabled       : false,
            releaseZipAlignEnabled   : true,
            debugZipAlignEnabled     : false

    ]

    java = [
            "sourceCompatibility": JavaVersion.VERSION_1_8,
            "targetCompatibility": JavaVersion.VERSION_1_8
    ]

    sign = [
            "keyPassword"  : "123456",
            "keyAlias"     : "xwh",
            "storePassword": "123456",
            "storeFile"    : "../module.jks"
    ]

    dependencies = [
            "appcompatVersion"       : "1.0.2",
            "constraintlayoutVersion": "1.1.3",
            "runnerVersion"          : "1.2.0",
            "espressocoreVersion"    : "3.2.0",
            "corektxVersion"         : "1.0.2",
            "multidexVersion"        : "2.0.1",
            "junitVersion"           : "4.12",
            "arouterApiVersion"      : "1.4.1",
            "arouterCompilerVersion" : "1.2.2"

    ]
}
```
配置全部放在根目录的config.gradle文件中，然后在根目录下的build.gradle文件中进行引用

```
apply from: "config.gradle"
```
app module只负责集成，里面没有太多代码，只需要在app module的build.gradle添加下面代码即可

```
dependencies {
    if(!rootProject.ext.android.isModule) {
        implementation project(':module_main')
        implementation project(':module_login')
        implementation project(':module_me')
    }
}

```

module_base要特别注意如果组件中有引用其他第三方组件或者自己写的组件，并且还能被其他3个组件访问的话，引用必须要以api的方式，不能是implementation，比如

```
dependencies {
    api fileTree(dir: 'libs', include: ['*.jar'])
    api "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    api "androidx.appcompat:appcompat:$rootProject.ext.dependencies.appcompatVersion"
    api "androidx.core:core-ktx:$rootProject.ext.dependencies.corektxVersion"
    api "androidx.constraintlayout:constraintlayout:$rootProject.ext.dependencies.constraintlayoutVersion"
    api "junit:junit:$rootProject.ext.dependencies.junitVersion"
    api "androidx.test:runner:$rootProject.ext.dependencies.runnerVersion"
    api "androidx.test.espresso:espresso-core:$rootProject.ext.dependencies.espressocoreVersion"
    api "androidx.multidex:multidex:$rootProject.ext.dependencies.multidexVersion"
    //arouter api，只需在common组件中引入一次
    api "com.alibaba:arouter-api:$rootProject.ext.dependencies.arouterApiVersion"
    //arouter编译器插件，每个组件都需引入
    kapt "com.alibaba:arouter-compiler:$rootProject.ext.dependencies.arouterCompilerVersion"
}
```
举个例子，A组件引用B组件，B组件引用C组件，如果A组件想使用C组件中的方法，那么B组件引用C组件的时候必须用api，如果用implementation ，A组件是不能访问到C组件的方法的。
module_base组件是公共的，供其他3个组件使用，所以要在每个需要使用它的组件中进行引用

```
implementation project(':module_base')
```

业务组件module的配置,比如module_login的build.gradle
```
if (rootProject.ext.android.isModule) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'

android {
    compileSdkVersion rootProject.ext.android.compileSdkVersion
    //如果资源文件有相同的名字，前面module的资源文件将替换后面的，Manifest会合并,
    // 因此在资源文件前面加前缀(注意：AS不会自动帮我们的资源名自动加上前缀，
    // 原来写了这段代码之后，AS会约束我们定义资源时，要我们手动加上前缀，否则报错！！所以只是起约束作用！！)
    resourcePrefix "login_"
    defaultConfig {
        minSdkVersion rootProject.ext.android.minSdkVersion
        targetSdkVersion rootProject.ext.android.targetSdkVersion
        versionCode rootProject.ext.android.versionCode
        versionName rootProject.ext.android.versionName

        testInstrumentationRunner rootProject.ext.android.testInstrumentationRunner

    }

    signingConfigs {
        release {
            keyAlias rootProject.ext.sign.keyAlias
            keyPassword rootProject.ext.sign.keyPassword
            storeFile file(rootProject.ext.sign.storeFile)
            storePassword rootProject.ext.sign.storePassword
        }
    }

    buildTypes {
        release {
            debuggable rootProject.ext.android.releaseDebuggable
            jniDebuggable rootProject.ext.android.releaseDebuggable
            zipAlignEnabled rootProject.ext.android.releaseZipAlignEnabled
            if (rootProject.ext.android.isModule) {
                //Resource shrinker cannot be used for libraries
                shrinkResources rootProject.ext.android.releaseShrinkResources
            }
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
        debug {
            debuggable rootProject.ext.android.debugDebuggable
            jniDebuggable rootProject.ext.android.debugDebuggable
            zipAlignEnabled rootProject.ext.android.debugZipAlignEnabled
            if (rootProject.ext.android.isModule) {
                //Resource shrinker cannot be used for libraries
                shrinkResources rootProject.ext.android.debugShrinkResources
            }
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
    }

    sourceSets {
        main {
            if (rootProject.ext.android.isModule) {
                manifest.srcFile 'src/main/module/AndroidManifest.xml'
            } else {
                manifest.srcFile 'src/main/AndroidManifest.xml'
                //集成开发模式下排除module文件夹中的所有文件
                java {
                    exclude 'module/**'
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility rootProject.ext.java.sourceCompatibility
        targetCompatibility rootProject.ext.java.targetCompatibility
    }

}

//配置arouter编译器参数，每个组件都需配置
kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.getName())
    }
}

dependencies {
    implementation project(':module_base')
    //arouter编译器插件，每个组件都需引入
    kapt "com.alibaba:arouter-compiler:$rootProject.ext.dependencies.arouterCompilerVersion"
}


```

1. 3个业务组件，究竟是一个单独的可以运行的组件还是一个library，我们可以通过前面定义的isModule来判断
1. 如果资源文件有相同的名字，前面module的资源文件将替换后面的，Manifest会合并, 为了避免集成模式下的命名冲突,因此在资源文件前面加前缀(注意：AS不会自动帮我们的资源名自动加上前缀，
   原来写了"resourcePrefix "login_""这段代码之后，AS会约束我们定义资源时，要我们手动加上前缀，否则报错！！所以只是起约束作用！！)
1. 当module属性为library时，不能设置applicationId；当为app时，如果未设置applicationId，默认包名为applicationId，所以为了方便，此处不设置applicationId
1. Android Studio会为每个module生成对应的AndroidManifest.xml文件，声明自身需要的权限、四大组件、数据等内容；当module属性为app时，其对应的AndroidManifest.xml需要具备完整app所需要的所有配置，尤其是声明Application和launch的Activity；当module属性为library时，如果每个组件都声明自己的Application和launch的Activity，那在合并的时候就会发生冲突，编译也不会通过，所以，就需要为当前module重新定义一个AndroidManifest.xml文件，不声明Application和launch的Activity，然后根据isModule的值指定AndroidManifest.xml的路径。

下图是该module的目录

![](/img/module3.png)

在需要切换module属性的时候改变config.gradle文件中isModule处声明的变量值，然后重新编译即可

#### 四、组件之间页面跳转(路由ARouter)
在组件化架构中，不同的组件之间是平衡的，不存在相互依赖的关系(可参考文章开头的架构图)。因此，假设在组件A中，想要跳转到组件B中的页面，如果使用Intent显式跳转就行不通了，而且大家都知道，Intent隐式跳转管理起来非常不方便，所以ARouter出现了，并且有强大的技术团队支持，可以放心使用了。那么如何在组件化架构中应用ARouter呢？

- 依赖处理

在module_base组件中将ARouter依赖进来，并配置编译参数；在业务组件中引入ARouter编译器插件，同时配置编译器参数，下面是module_base组件gradle文件的部分片段
```
//配置arouter编译器参数，每个组件都需配置
kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.getName())
    }
}

dependencies {
    api fileTree(dir: 'libs', include: ['*.jar'])
    api "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    api "androidx.appcompat:appcompat:$rootProject.ext.dependencies.appcompatVersion"
    api "androidx.core:core-ktx:$rootProject.ext.dependencies.corektxVersion"
    api "androidx.constraintlayout:constraintlayout:$rootProject.ext.dependencies.constraintlayoutVersion"
    api "junit:junit:$rootProject.ext.dependencies.junitVersion"
    api "androidx.test:runner:$rootProject.ext.dependencies.runnerVersion"
    api "androidx.test.espresso:espresso-core:$rootProject.ext.dependencies.espressocoreVersion"
    api "androidx.multidex:multidex:$rootProject.ext.dependencies.multidexVersion"
    //arouter api，只需在common组件中引入一次
    api "com.alibaba:arouter-api:1.4.1"
    //arouter编译器插件，每个组件都需引入
    kapt "com.alibaba:arouter-compiler:1.2.2"
}

```
"kapt "com.alibaba:arouter-compiler:1.2.2""必须在每个可跳转的module中引用，因为他会根据注解的类在build中生成对应的文件，比如在module_me中有这样一段代码

```
@Route(path = ARouterPath.MODULEME_MEACTIVITY)
class MeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_me)
        ARouter.getInstance().inject(this)
        Toast.makeText(applicationContext, intent.extras.getString("param"), Toast.LENGTH_LONG).show()
    }
}
```
那么就会在相应module生成相应的文件

![](/img/module4.png)

如果我们没有在每个注解的module中添加ARouter依赖，那么在相应的module中就不会生成相应的文件，跳转永远也都不会成功。

- 初始化及编码实现

在组件架构中，经常会遇到组件需要使用全局Context的情况，当组件属性为app时，可以通过自定义Application实现；当组件属性为library时，由于组件被app依赖，导致无法调用app的Application实例，而且自身不存在Application；所以，这里给出的方案是在module_base组件中创建一个BaseApplication，然后让组件模式下的Application继承BaseApplication,在BaseApplication中获取全局Context，并做一些初始化的工作，这里需要初始化ARouter，如下是在module_base组件中声明的BaseApplication。

```
/**
 *描述：Application基类
 *
 */
class BaseApplication : Application() {

    companion object {
        var mContext: Application? = null
        //获取全局Context
        fun getContext(): Application {
            return mContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this

        //初始化Arouter
        initARouter()
        //初始化其他第三方库
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        //清理Arouter注册表
        ARouter.getInstance().destroy()
    }

    private fun initARouter() {
        if (BuildConfig.DEBUG) {  // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()    // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)// 尽可能早，推荐在Application中初始化
    }


}
```
初始化之后，就可以通过@Route注解注册页面，然后调用ARouter api实现页面的跳转了（这里所谓的跨组件页面跳转是指在集成模式下，而非组件模式下）
```
@Route(path = ARouterPath.MODULEME_MEACTIVITY)
class MeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_me)
        //注入传递的参数
        ARouter.getInstance().inject(this)
        Toast.makeText(applicationContext, intent.extras.getString("param"), Toast.LENGTH_LONG).show()
    }
}
```
然后在其他页面调用如下方法就可以跳转到上面路由path指定的目标页面了
```
ARouter.getInstance().build(ARouterPath.MODULEME_MEACTIVITY).withString("param", "我要到个人中心页面").navigation()
```
以上便完成了一次简单的跨越组件的页面跳转，仅仅是ARouter的基本使用而已。
#### 五、关于混淆
说到混淆，有人可能会疑惑，如果在各个组件中混淆可不可以？不建议这样混淆！！因为组件在集成模式下被gradle构建成了release类型的aar包，如果在组件中进行混淆，一旦代码出现了bug，这个时候就很难根据日志去追踪bug产生的原因，而且不同组件分别进行混淆非常不方便维护和修改，这也是不推荐在业务组件中配置buildType(构建类型)的原因。

所以，组件化项目的代码混淆放在集成模式下的app壳工程，各个业务组件不配置混淆。集成模式下在app壳工程.gradle文件的release构建模式下开启混淆，其他buildType配置和普通项目相同，混淆文件放在app壳工程下，各个组件的代码混淆均放在该混淆文件中。
#### 六、小结
- 解耦：将业务组件代码与工程解耦。
- 提高开发效率：依赖解耦这一优势，团队成员可以只专注于自己负责的组件，开发效率更高；而且，组件开发过程中只需编译自身的module，这样大大缩短了编译时长，避免了漫长的等待编译局面。
- 结构清晰：在业务组件明确拆分的前提下，项目结构变的异常清晰，非常方便全局掌控。
#### 七、关注及反馈
倘若本项目对你有一丝丝的帮助和价值，烦请给个star,或者有什么好的建议或意见，也可以发个issues，谢谢！
