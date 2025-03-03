'use client'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import DOMPurify from 'dompurify'
import Image from 'next/image'
import React, { useEffect, useRef, useState } from 'react'
import { LoaderIcon } from 'react-hot-toast'

import privateAPI from '@/api/privateAPI/index.ts'
import { ISSUE_CONSTANTS } from '@/constants/issue/index.ts'
import useLangStore from '@/hooks/useLangStore.ts'
import LikeOffSVG from '@/static/svg/issue/issue-post-like-off-icon.svg'
import LikeOnSVG from '@/static/svg/issue/issue-post-like-on-icon.svg'
import ScrapOffSVG from '@/static/svg/issue/issue-post-scrap-off-icon.svg'
import ScrapOnSVG from '@/static/svg/issue/issue-post-scrap-on-icon.svg'
import UnLikeOffSVG from '@/static/svg/issue/issue-post-unlike-off-icon.svg'
import UnLikeOnSVG from '@/static/svg/issue/issue-post-unlike-on-icon.svg'
import TTSSVG from '@/static/svg/issue/issue-tts-icon.svg'

function IssueDetail({ params }: { params: { postId: string } }) {
  const [scraped, setScraped] = useState<boolean | null>(null)
  const [action, setAction] = useState<number | null>(null) //1: like, 2: unlike 0: none
  const scrollPos = useRef<HTMLDivElement>(null)
  const [audio, setAudio] = useState<HTMLAudioElement | null>(null)

  const [isCollapsed, setIsCollapsed] = useState(false)

  const lang = useLangStore((state) => state.lang)
  const santinizer = DOMPurify.sanitize
  const { data: shorformGrid, isLoading } = useQuery({
    queryKey: ['short-form-grid-detail', params.postId],
    queryFn: async () => {
      const res = await privateAPI.get('/shortForm/' + params.postId)

      if (res.status === 200) {
        //오디오 플레이
        const tempAudio = new Audio(res.data.result.audio)
        setAudio(tempAudio)
      }

      if (res.status === 200) {
        setScraped(res.data.result.is_saved)
        setAction(res.data.result.preference)
      }

      return res.data.result
    },
    staleTime: 0,
  })

  const startTime = useRef<string | null>(null)
  const endTime = useRef<string | null>(null)

  useEffect(() => {
    const tempStart = new Date().toISOString()
    startTime.current = tempStart

    const handleUnload = async () => {
      const tempEnd = new Date().toISOString()
      endTime.current = tempEnd
      await privateAPI.post('/shortForm/log', {
        shortFormId: Number(params.postId),
        startAt: startTime.current,
        endAt: endTime.current,
      })
    }

    return () => {
      handleUnload()
    }
  }, [params.postId])

  useEffect(() => {
    return () => {
      if (audio) {
        audio.pause()
        audio.currentTime = 0
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const queryClient = useQueryClient()
  const { mutate: mutateScrap } = useMutation({
    mutationFn: async () => {
      const res = await privateAPI.post('/shortForm/is_saved', {
        shortFormId: params.postId,
        isSaved: !scraped,
      })
      return res.data.result
    },
    onSuccess: () => {
      setScraped(!scraped)
      queryClient.invalidateQueries({
        queryKey: ['short-form-grid-detail'],
      })
      queryClient.invalidateQueries({
        queryKey: ['short-form-grid-detail', params.postId],
      })
      queryClient.invalidateQueries({
        queryKey: ['short-form-grid-scrap'],
      })
    },
  })

  const handleScrap = () => {
    mutateScrap()
  }

  const { mutate: mutateLike } = useMutation({
    mutationFn: async ({ num }: { num: number }) => {
      const res = await privateAPI.post('/shortForm/preference', {
        shortFormId: Number(params.postId),
        preference: num,
      })
      return res.data.result
    },

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['short-form-grid-detail'],
      })
      queryClient.invalidateQueries({
        queryKey: ['short-form-grid-detail', params.postId],
      })
      queryClient.invalidateQueries({
        queryKey: ['short-form-grid-like'],
      })
    },
  })

  const handleLike = () => {
    if (action === 1) {
      setAction(0)
      mutateLike({ num: 0 })
    } else {
      setAction(1)
      mutateLike({ num: 1 })
    }
  }

  const handleUnLike = () => {
    if (action === 2) {
      setAction(0)
      mutateLike({ num: 0 })
    } else {
      setAction(2)
      mutateLike({ num: 2 })
    }
  }

  const handleRadio = () => {
    if (audio) {
      audio.play()
    }
  }
  if (isLoading) {
    return (
      <div className="flex w-full grow items-center justify-center py-10">
        <LoaderIcon />
      </div>
    )
  }
  return (
    <div className="relative w-full grow overflow-hidden py-4">
      {/* 검정 화면 wrapper */}
      {isCollapsed && (
        <button
          onClick={() => setIsCollapsed(false)}
          className="absolute bottom-0 z-30 h-full w-full bg-black bg-opacity-70"
        />
      )}
      <Image
        src={shorformGrid.image}
        alt="new jeans"
        layout="fill"
        className="h-full w-full object-contain"
      />

      <div className="absolute bottom-0 z-50 flex w-full items-end justify-between">
        {/* text */}
        <button
          onClick={() => {
            scrollPos.current?.scrollTo(0, 0)
            setIsCollapsed(!isCollapsed)
          }}
          className={`${isCollapsed ? 'h-[20dvh]' : 'h-[6dvh]'} flex flex-1 flex-col gap-y-2 px-4 transition-all duration-300 ease-in-out`}
        >
          {/* <p className="text-xs-bold">타이틀</p> */}
          <div
            className={`${isCollapsed ? 'overflow-y-scroll' : 'line-clamp-2 overflow-y-hidden'} w-full whitespace-pre-line text-start text-2xs`}
            // 스크롤 탑으로
            ref={scrollPos}
            dangerouslySetInnerHTML={{
              __html: santinizer(shorformGrid.content),
            }}
          />
        </button>
        {/* icons */}
        <div className="flex flex-col gap-y-5 px-4 py-5">
          <button
            onClick={handleRadio}
            disabled={!audio}
            className="flex flex-col items-center justify-center gap-y-1"
          >
            <TTSSVG className="h-6 w-6" />
            <span className="text-2xs">
              {ISSUE_CONSTANTS[lang]['detail-tts-text']}
            </span>
          </button>
          <button
            onClick={handleLike}
            className="flex flex-col items-center justify-center gap-y-1"
          >
            {action && action === 1 ? (
              <LikeOnSVG className="h-6 w-6" />
            ) : (
              <LikeOffSVG className="h-6 w-6" />
            )}
            <span className="text-2xs">
              {ISSUE_CONSTANTS[lang]['detail-like-text']}
            </span>
          </button>

          <button
            onClick={handleUnLike}
            className="flex flex-col items-center justify-center gap-y-1"
          >
            {action && action === 2 ? (
              <UnLikeOnSVG className="h-6 w-6" />
            ) : (
              <UnLikeOffSVG className="h-6 w-6" />
            )}
            <span className="text-2xs">
              {ISSUE_CONSTANTS[lang]['detail-unlike-text']}
            </span>
          </button>

          <button
            onClick={handleScrap}
            className="flex flex-col items-center justify-center gap-y-1"
          >
            <div className="flex h-6 w-6 items-center justify-center">
              {scraped && scraped ? <ScrapOnSVG /> : <ScrapOffSVG />}
            </div>
            <span className="text-2xs">
              {ISSUE_CONSTANTS[lang]['detail-scrap-text']}
            </span>{' '}
          </button>
        </div>
      </div>
    </div>
  )
}

export default IssueDetail
