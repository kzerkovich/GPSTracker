plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.com.google.devtools.ksp)
}

android {
	namespace = "com.kzerk.gpstracker"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.kzerk.gpstracker"
		minSdk = 21
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}

	buildFeatures {
		viewBinding {
			enable = true
		}
	}
}

dependencies {
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.constraintlayout)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)

	implementation(libs.androidx.preference.ktx)
	implementation(libs.osmbonuspack)
	implementation(libs.osmdroid)
	implementation(libs.play.service.location)
	implementation(libs.androidx.room)
	ksp(libs.room.compiler)
	implementation(libs.lifecycle.livedata)
	implementation(libs.lifecycle.viewmodel)
}