// 대피소 상세 정보 모음 (대피소 정보들 + 수정 버튼)
import React, { Fragment, useState } from "react";
import { Dialog, Transition } from "@headlessui/react";

import MapAdminInfoBox from "./MapAdminInfoBox";

import Button from "../@common/Button";
import Text from "../@common/Text";

interface MapAdminInfoBoxListProps {
  className?: string;
}

const MapAdminInfoBoxList = ({ className }: MapAdminInfoBoxListProps) => {
  const [infoModal, setInfoModal] = useState(false);

  const handleEditInfoClick = () => {
    console.log("대피소 상세 정보 모음의 수정 버튼을 클릭했습니다.");
    setInfoModal(true);
  };

  const handleCloseModalClick = () => {
    console.log("대피소 상세 정보 모음의 닫기 버튼을 클릭했습니다.");
    setInfoModal(false);
  };

  const handleEditModalClick = () => {
    console.log("모달 내의 콘텐츠 중, 수정 버튼을 눌렀습니다.");
  };

  /** 모달창 나왔을 때, background 클릭하면 그냥 return */
  const handleBackClick = () => {
    return;
  };

  return (
    <>
      <div className="px-2 flex flex-col justify-between h-full">
        <MapAdminInfoBox />
        <MapAdminInfoBox />
        <MapAdminInfoBox />
        {/* 규모 + 최대 수용인원 */}
        <div className="flex justify-between">
          <MapAdminInfoBox />
          <MapAdminInfoBox />
        </div>
        {/* 위험성 + 혼잡도 */}
        <div className="flex justify-between">
          <MapAdminInfoBox />
          <MapAdminInfoBox />
        </div>
        <div className="flex justify-between">
          <MapAdminInfoBox />
          <Button
            size="Small"
            color="Sub"
            onClick={handleEditInfoClick}
            isActive={true}
            className="w-20"
          >
            수정
          </Button>
        </div>
      </div>

      {/* 수정 모달창 */}
      <Transition appear show={infoModal} as={Fragment}>
        <Dialog as="div" className="relative z-10" onClose={handleBackClick}>
          <Transition.Child
            as={Fragment}
            enter="ease-out duration-300"
            enterFrom="opacity-0"
            enterTo="opacity-100"
            leave="ease-in duration-200"
            leaveFrom="opacity-100"
            leaveTo="opacity-0"
          >
            <div className="fixed inset-0 bg-black/60" />
          </Transition.Child>

          <div className="fixed inset-0 overflow-y-auto">
            <div className="flex min-h-full items-center justify-center p-4 text-center">
              <Transition.Child
                as={Fragment}
                enter="ease-out duration-300"
                enterFrom="opacity-0 scale-95"
                enterTo="opacity-100 scale-100"
                leave="ease-in duration-200"
                leaveFrom="opacity-100 scale-100"
                leaveTo="opacity-0 scale-95"
              >
                <Dialog.Panel className="w-1/2 transform overflow-hidden rounded-2xl bg-white p-7 text-left align-middle shadow-xl transition-all">
                  <Text size="text3" isBold={true} className="text-center">
                    대피소 상태 변경
                  </Text>

                  {/* 내용 묶음 */}
                  <div className="w-4/5 h-80 mx-auto mt-8 flex flex-col justify-between">
                    <MapAdminInfoBox />
                    <MapAdminInfoBox />
                    <MapAdminInfoBox />
                    <MapAdminInfoBox />
                    <MapAdminInfoBox />
                    <MapAdminInfoBox />
                    <MapAdminInfoBox />
                  </div>

                  {/* 버튼 묶음 (수정, 취소) */}
                  <div className="mt-8 w-1/3 grid grid-cols-2 gap-2 mx-auto">
                    <Button
                      size="Medium"
                      color="Main"
                      isActive={true}
                      onClick={handleEditModalClick}
                    >
                      수정
                    </Button>
                    <Button
                      size="Medium"
                      color="Sub"
                      isActive={true}
                      onClick={handleCloseModalClick}
                    >
                      취소
                    </Button>
                  </div>
                </Dialog.Panel>
              </Transition.Child>
            </div>
          </div>
        </Dialog>
      </Transition>
    </>
  );
};

export default MapAdminInfoBoxList;
