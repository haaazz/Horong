import {
  PHASE_DEVELOPMENT_SERVER,
  PHASE_PRODUCTION_BUILD,
} from 'next/constants.js'

/** @type {import("next").NextConfig} */
const nextConfig = {
  swcMinify: true,
  webpack(config) {
    config.module.rules.push({
      test: /\.svg$/,

      use: ['@svgr/webpack'],
    })

    return config
  },
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        pathname: '/**',
        hostname: 'horong-service.s3.ap-northeast-2.amazonaws.com',
      },
      {
        protocol: 'https',
        pathname: '/**',
        hostname: 'horong-service.s3.amazonaws.com',
      },
    ],
  },
}

const nextConfigFunction = async (phase) => {
  if (phase === PHASE_DEVELOPMENT_SERVER || phase === PHASE_PRODUCTION_BUILD) {
    const withPWA = (await import('@ducanh2912/next-pwa')).default({
      dest: 'public',
    })
    return withPWA(nextConfig)
  }
  return nextConfig
}

export default nextConfigFunction
