'use strict'

import { app, BrowserWindow, globalShortcut } from 'electron'
import * as path from 'path'
import config from 'common/config'

const isDevelopment = process.env.NODE_ENV !== 'production'
const icon = path.join(__static, 'icons/icon.ico')
let mainWindow

function createMainWindow () {
  const window = new BrowserWindow({
    useContentSize: true,
    show: false,
    webPreferences: {
      webSecurity: false,
      nodeIntegration: config.node,
      nodeIntegrationInWorker: config.worker
    },
    alwaysOnTop: !isDevelopment,
    icon
  })
  if (isDevelopment) {
    window.webContents.openDevTools()
  }
  window.loadURL(`${config.url}`)
  window.on('closed', () => {
    mainWindow = null
  })
  window.on('ready-to-show', () => {
    window.show()
    window.maximize()
    window.focus()
    window.setFullScreen(!isDevelopment)
  })
  return window
}

const gotTheLock = app.requestSingleInstanceLock()
if (!gotTheLock) {
  app.quit()
} else {
  app.on('second-instance', (event, commandLine, workingDirectory) => {
    if (mainWindow) {
      if (mainWindow.isMinimized()) mainWindow.restore()
      mainWindow.focus()
    }
  })
  app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
      globalShortcut.unregisterAll()
      app.quit()
    }
  })
  app.on('activate', () => {
    if (mainWindow === null) {
      mainWindow = createMainWindow()
    }
  })

  app.on('ready', () => {
    mainWindow = createMainWindow()
    globalShortcut.register('F5', () => {
      if (mainWindow) {
        mainWindow.reload()
      }
    })
    globalShortcut.register('F11', () => {
      if (mainWindow) {
        mainWindow.setFullScreen(!mainWindow.isFullScreen())
      }
    })
    globalShortcut.register('F10', () => {
      if (mainWindow) {
        mainWindow.webContents.toggleDevTools()
        setImmediate(() => {
          let dev = mainWindow.webContents.isDevToolsOpened()
          mainWindow.setAlwaysOnTop(!dev)
        })
      }
    })
  })
}
