# Rsporosh TV — কম্পিউটারে Android Studio ছাড়া APK তৈরি

1. GitHub-এ একটি নতুন repository তৈরি করুন (Private রাখলে সোর্স কোড ব্যক্তিগত থাকবে)।
2. এই ZIP Extract করে **RsporoshTV ফোল্ডারের ভেতরের সব ফাইল**, `.github` সহ, repository-র root-এ upload করুন। GitHub web interface hidden `.github` folder আপলোড করতে অসুবিধা হলে GitHub Desktop বা অন্য git client ব্যবহার করুন।
3. Repository-তে **Actions** tab → **Build Rsporosh TV APK** → **Run workflow** নির্বাচন করুন। প্রথমবার GitHub Actions অনুমোদন করতে হতে পারে।
4. Build সফল হলে একই run-এর **Artifacts** থেকে **RsporoshTV-debug-APK** ডাউনলোড করুন। ZIP খুললে `app-debug.apk` পাবেন।
5. এটি কেবল **debug APK**, পরীক্ষা করার জন্য। পাবলিক অ্যাপ স্টোরে প্রকাশের জন্য release signing, পরীক্ষা ও privacy review প্রয়োজন।

নোট: APK এখানে compile করা হয়নি। GitHub Actions-এ compile হতে হবে। সাধারণ Android-এ boot-এর পর অ্যাপ স্বয়ংক্রিয়ভাবে foreground-এ আসা নিশ্চিত নয়; managed kiosk / supported TV launcher লাগতে পারে।
