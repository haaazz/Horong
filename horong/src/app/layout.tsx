import type { Metadata, Viewport } from 'next'

import { Noto_Sans_JP, Noto_Sans_SC } from 'next/font/google'
import localFont from 'next/font/local'
import { Suspense } from 'react'
import { Toaster } from 'react-hot-toast'

import { NavigationEvents } from '@/components/navigationEvents/index.tsx'
import TanstackQueryProvider from '@/providers/TanstackQueryProvider/index.tsx'
import './globals.css'

const notoSansChinese = Noto_Sans_SC({
  variable: '--font-noto-sans-sc',
  weight: ['100', '900'],
  style: 'normal',
  subsets: ['cyrillic', 'latin', 'latin-ext', 'vietnamese'],
})

const notoSansJp = Noto_Sans_JP({
  variable: '--font-noto-sans-jp',
  weight: ['100', '900'],
  style: 'normal',
  subsets: ['cyrillic', 'latin', 'latin-ext', 'vietnamese'],
})

const geistSans = localFont({
  src: './fonts/GeistVF.woff',
  variable: '--font-geist-sans',
  weight: '100 900',
})
const geistMono = localFont({
  src: './fonts/GeistMonoVF.woff',
  variable: '--font-geist-mono',
  weight: '100 900',
})
const pretendard = localFont({
  src: './fonts/PretendardVariable.woff2',
  weight: '45 920',
  variable: '--font-pretendard',
})

const APP_NAME = 'Horong'
const APP_DEFAULT_TITLE = 'Horong - guide app for exchange student in Korea'
const APP_TITLE_TEMPLATE = '%s - Horong PWA App'
const APP_DESCRIPTION = 'guide app for exchange student in Korea'

export const metadata: Metadata = {
  applicationName: APP_NAME,
  title: {
    default: APP_DEFAULT_TITLE,
    template: APP_TITLE_TEMPLATE,
  },
  description: APP_DESCRIPTION,
  manifest: '/manifest.json',
  appleWebApp: {
    capable: true,
    statusBarStyle: 'default',
    title: APP_DEFAULT_TITLE,
    // startUpImage: [],
  },
  formatDetection: {
    telephone: false,
  },
  openGraph: {
    type: 'website',
    siteName: APP_NAME,
    title: {
      default: APP_DEFAULT_TITLE,
      template: APP_TITLE_TEMPLATE,
    },
    description: APP_DESCRIPTION,
  },
  twitter: {
    card: 'summary',
    title: {
      default: APP_DEFAULT_TITLE,
      template: APP_TITLE_TEMPLATE,
    },
    description: APP_DESCRIPTION,
  },
}
export const viewport: Viewport = {
  themeColor: '#FFFFFF',
  width: 'device-width',
  initialScale: 1,
  minimumScale: 1,
  maximumScale: 1,
  userScalable: false,
  viewportFit: 'cover',
}
export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html
      lang="en"
      className="h-full overscroll-none bg-white"
    >
      <body
        className={`h-full w-full overflow-hidden bg-grey-90 ${notoSansChinese.variable} ${notoSansJp.variable} ${pretendard.variable} ${geistSans.variable} ${geistMono.variable} min-h-dvh antialiased`}
      >
        <TanstackQueryProvider>
          {children}
          <Toaster />
          <Suspense fallback={null}>
            <NavigationEvents />
          </Suspense>
          <div id="modalPortal" />
        </TanstackQueryProvider>
      </body>
    </html>
  )
}
