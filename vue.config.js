module.exports = {
  lintOnSave: false,
  devServer:{
    proxy:{
      '/api':{ // 請求前綴有api,就轉發給「http://localhost:8010」伺服器
        target:'http://localhost:8010',
        pathRewrite:{'^/api':''}, // 將api(含)之前的前綴,都轉成空字串
        ws:true, // 用於支持websocket,默認true
        changeOrigin:true, // 默認true,用於控制請求頭中的host值
      },
    }
  }
}   