// 대피소 상세 정보 모음 (대피소 정보들 + 수정 버튼)
import React, { Fragment, useState } from "react";
import { Dialog, Transition } from "@headlessui/react";

import styled from "styled-components";
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
      <div>
        대피소 상세 정보 모음 (대피소 정보들 + 수정 버튼)
        <div>대피소 정보들 들어가요</div>
        <Button
          size="Medium"
          color="Sub"
          onClick={handleEditInfoClick}
          isActive={true}
        >
          수정
        </Button>
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
                <Dialog.Panel className="w-full w-3/4 transform overflow-hidden rounded-2xl bg-white p-7 text-left align-middle shadow-xl transition-all">
                  <Text size="text3" isBold={true} className="text-center">
                    대피소 상태 변경
                  </Text>
                  <div className="mt-2">
                    <p className="text-sm text-gray-500">
                      Your payment has been successfully submitted. We’ve sent
                      you an email with all of the details of your order.
                    </p>
                  </div>

                  {/* 버튼 묶음 (수정, 취소) */}
                  <div className="mt-4 w-1/3 grid grid-cols-2 gap-2 mx-auto">
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

const StyledModalBackground = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
`;

const StyledModalContent = styled.div`
  width: 60%;
  height: 75%;
  position: absolute;
  border-radius: 30px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
`;
