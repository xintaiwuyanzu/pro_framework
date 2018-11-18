import stripJsonComments from 'strip-json-comments'
import fs from 'fs'
import * as path from 'path'
// 读取配置文件
const pathToConfig = path.join(__static, '/config.json')
const fileContents = fs.readFileSync(pathToConfig, 'utf-8')
const config = JSON.parse(stripJsonComments((fileContents)))
console.log(config)
console.log(pathToConfig)
export default config
