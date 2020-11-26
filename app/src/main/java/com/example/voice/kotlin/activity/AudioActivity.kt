package com.example.voice.kotlin.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.media.AudioTrack
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Log.d
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voice.R
import com.example.voice.utils.GlobalConfig
import com.example.voice.utils.GlobalConfig.Companion.AUDIO_FORMAT
import com.example.voice.utils.GlobalConfig.Companion.CHANNEL_CONFIG
import com.example.voice.utils.GlobalConfig.Companion.SAMPLE_RATE_INHZ
import com.example.voice.utils.PcmToWavUtil
import java.io.*


class AudioActivity : AppCompatActivity(){

    private val TAG = "TAG_AudioActivity"
    private val MY_PERMISSIONS_REQUEST = 1001

    private lateinit var audioRecord: AudioRecord  // 声明 AudioRecord 对象
    private var recordBufSize = 0  // 声明 recordBuffer 的大小字段
    private var isRecording = false

    private lateinit var audioTrack: AudioTrack
    private lateinit var audioData: ByteArray
    private lateinit var fileInputStream: FileInputStream

    /**
     * 需要申请的运行时权限
     */
    private val permissions = arrayOf<String>(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    /**
     * 被用户拒绝的权限列表
     */
    private val mPermissionList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.voice.R.layout.activity_audio)

        checkPermissions()
    }

    private fun checkPermissions() {
        // Marshmallow 开始才用申请运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsSize = permissions.size
            for (i in 0 until permissionsSize) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i])
                }
            }
            if (!mPermissionList.isEmpty()) {
                val permissions = mPermissionList.toArray(arrayOfNulls<String>(mPermissionList.size))
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            val grantResultsSize = grantResults.size
            for (i in 0 until grantResultsSize) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, permissions[i] + "权限被用户禁止！")
                }
            }
            // 运行时权限的申请不是本demo的重点，所以不再做更多的处理，请同意权限申请。
        }
    }

    fun btnStart(view: View) {
        // 获取buffer的大小并创建AudioRecord
        // 采样率，声道数，返回的音视频数据的格式
        recordBufSize = AudioRecord.getMinBufferSize(GlobalConfig.SAMPLE_RATE_INHZ,
                GlobalConfig.CHANNEL_CONFIG, GlobalConfig.AUDIO_FORMAT)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, GlobalConfig.SAMPLE_RATE_INHZ,
                GlobalConfig.CHANNEL_CONFIG, GlobalConfig.AUDIO_FORMAT, recordBufSize)

        // 初始化一个buffer
        val data = ByteArray(recordBufSize)

        // getExternalFilesDir：SDCard/Android/data/应用的包名/files/ 目录
        val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
        Log.d(TAG, file.absolutePath) // /storage/emulated/0/Android/data/com.example.testdemo/files/Music/test.pcm
        if (!file.mkdirs()) {
            Log.d(TAG, "Directory not created")
        }
        if (file.exists()) {
            file.delete()
        }

        // 开始录音
        audioRecord.startRecording()
        isRecording = true

        // pcm 数据无法直接播放，需要保存为WAV格式。
        Thread(Runnable {
            // 创建一个数据流，一边从AudioRecord中读取声音数据到初始化的buffer，一边将buffer中数据导入数据流
            var os: FileOutputStream? = null
            try {
                os = FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            if (null != os) {
                while (isRecording) {
                    val read = audioRecord.read(data, 0, recordBufSize)
                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            os.write(data)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                try {
                    Log.d(TAG, "run: close file output stream !")
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    fun btnStop(view: View) {
        // 关闭数据流，修改标志位：isRecording 为false，上面的while循环就自动停止了，数据流也就停止流动了，Stream也就被关闭了。
        isRecording = false
        // 停止录音，释放资源。
        audioRecord.stop()
        audioRecord.release()
    }

    /**
     * pcm 转为 wav
     * 为 pcm 添加 Head 数据
     */
    fun addHead(view: View){
        val pcmToWavUtil = PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
        val pcmFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
        val wavFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.wav")
        if (!wavFile.mkdirs()) {
            Log.e(TAG, "wavFile Directory not created")
        }
        if (wavFile.exists()) {
            wavFile.delete()
        }
        pcmToWavUtil.pcmToWav(pcmFile.absolutePath, wavFile.absolutePath)
    }

    /**
     * 播放，使用static模式
     */
    fun btnPayInSTATIC(view: View){
        // static 模式，需要将音频数据一次性 write 到 AudioTrack 的内部缓冲区
        // 这里应该使用协程来代替 AsyncTask。
        MyAsyncTask().execute()
    }

    inner class MyAsyncTask : AsyncTask<Void, Int, Void>() {

        override fun onPreExecute() { }

        override fun doInBackground(vararg param: Void?): Void? {
            try {
                val inputStream = resources.openRawResource(R.raw.ding)
                try {
                    val out = ByteArrayOutputStream()
                    var b: Int
                    while (inputStream.read().also { b = it } != -1) {
                        out.write(b)
                    }
                    d(TAG, "Got the data")
                    audioData = out.toByteArray()
                } finally {
                    inputStream.close()
                }
            } catch (e: IOException) {
                Log.wtf(TAG, "Failed to read", e)
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) { }

        override fun onPostExecute(result: Void?) {
            Log.d(TAG, "Creating track...audioData.length = " + audioData.size)
            // R.raw.ding铃声文件的相关属性为 22050Hz, 8-bit, Mono
            audioTrack = AudioTrack(
                    AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    AudioFormat.Builder().setSampleRate(22050)
                            .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                    audioData.size,
                    AudioTrack.MODE_STATIC,
                    AudioManager.AUDIO_SESSION_ID_GENERATE)
            d(TAG, "Writing audio data...")
            audioTrack.write(audioData, 0, audioData.size)
            d(TAG, "Starting playback")
            audioTrack.play()
            d(TAG, "Playing")
        }

        override fun onCancelled() { }
    }

    /**
     * 播放，使用stream模式
     */
    fun btnPayInSTREAM(view: View){
        /*
        * SAMPLE_RATE_INHZ 对应pcm音频的采样率
        * channelConfig 对应pcm音频的声道
        * AUDIO_FORMAT 对应pcm音频的格式
        * */
        val channelConfig = AudioFormat.CHANNEL_OUT_MONO
        val minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT)
        audioTrack = AudioTrack(
                AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
                        .setEncoding(AUDIO_FORMAT)
                        .setChannelMask(channelConfig)
                        .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE)
        audioTrack.play()

        val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
        try {
            fileInputStream = FileInputStream(file)
            Thread(Runnable {
                try {
                    val tempBuffer = ByteArray(minBufferSize)
                    while (fileInputStream.available() > 0) {
                        val readCount = fileInputStream.read(tempBuffer)
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                            continue
                        }
                        if (readCount != 0 && readCount != -1) {
                            audioTrack.write(tempBuffer, 0, readCount)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object{
        fun actionStart(context: Context){
            val intent = Intent(context, AudioActivity::class.java)
            context.startActivity(intent)
        }
    }
}
