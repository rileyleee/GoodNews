import styled from "styled-components";
import Text from "../@common/Text";

// 지도 관련 설명
const SubIntro3 = () => {
  return (
    <StyledSubIntro3Wrapper>
      <StyledSubIntro3ContentWrapper className="flex items-center justify-center lg:justify-around relative">
        <StyledMapImage
          className="h-4/6 lg:h-5/6"
          src="/assets/subIntroImg.png"
          alt="지도 페이지 소개"
        />
        <div className="absolute md:relative bg-zinc-300/70 md:bg-inherit">
          {/* sm 타이틀 */}
          <Text size="text4" className="text-center md:hidden">
            <strong>지도</strong>로 필요한 정보를 한 눈에
          </Text>
          {/* md 타이틀 */}
          <Text
            size="text3"
            className="text-center hidden md:block lg:hidden text-center"
          >
            <strong>지도</strong>로 필요한 정보를 한 눈에
          </Text>
          {/* lg 타이틀 */}
          <Text size="text2" className="hidden lg:block text-center">
            <strong>지도</strong>로 필요한 정보를 한 눈에
          </Text>
          <div className="my-10 mx-12 grid grid-cols-3 gap-8">
            <div className="flex drop-shadow-lg justify-around items-center h-24">
              <StyledFilterImage src="/assets/mapShelter.png" />
              <Text className="hidden lg:block" size="text5">
                대피소
              </Text>
            </div>
            <div className="flex drop-shadow-lg justify-around items-center h-24">
              <StyledFilterImage src="/assets/mapHospital.png" />
              <Text className="hidden lg:block" size="text5">
                병원
              </Text>
            </div>
            <div className="flex drop-shadow-lg justify-around items-center h-24">
              <StyledFilterImage src="/assets/mapMart.png" />
              <Text className="hidden lg:block" size="text5">
                식료품점
              </Text>
            </div>
            <div className="flex drop-shadow-lg justify-around items-center h-24">
              <StyledFilterImage src="/assets/mapFamily.png" />
              <Text className="hidden lg:block" size="text5">
                가족 위치
              </Text>
            </div>
            <div className="flex drop-shadow-lg justify-around items-center h-24">
              <StyledFilterImage src="/assets/mapPromise.png" />
              <Text className="hidden lg:block" size="text5">
                약속 장소
              </Text>
            </div>
            <div className="flex drop-shadow-lg justify-around items-center h-24">
              <StyledFilterImage src="/assets/mapAroundPerson.png" />
              <Text className="hidden lg:block" size="text5">
                주변 사람 위치
              </Text>
            </div>
          </div>
          {/* sm 추가설명 */}
          <div className="text-center md:hidden">
            <Text size="text6" color="Gray">
              재난 상황에 맞는 대피소를 확인이 가능하며,
            </Text>
            <Text size="text6" color="Gray">
              사용자들의 실시간 위험 정보 공유를 통해
            </Text>
            <Text size="text6" color="Gray">
              대피소 상태를 최신화하여 피해를 줄일 수 있어요.
            </Text>
          </div>
          {/* md 추가설명 */}
          <div className="text-right hidden md:block lg:hidden">
            <Text size="text5" color="Gray">
              재난 상황에 맞는 대피소를 확인이 가능하며,
            </Text>
            <Text size="text5" color="Gray">
              사용자들의 실시간 위험 정보 공유를 통해
            </Text>
            <Text size="text5" color="Gray">
              대피소 상태를 최신화하여 피해를 줄일 수 있어요.
            </Text>
          </div>
          {/* lg 추가설명 */}
          <div className="hidden lg:block text-right">
            <Text size="text4" color="Gray">
              재난 상황에 맞는 대피소를 확인이 가능하며,
            </Text>
            <Text size="text4" color="Gray">
              사용자들의 실시간 위험 정보 공유를 통해
            </Text>
            <Text size="text4" color="Gray">
              대피소 상태를 최신화하여 피해를 줄일 수 있어요.
            </Text>
          </div>
        </div>
      </StyledSubIntro3ContentWrapper>
    </StyledSubIntro3Wrapper>
  );
};

export default SubIntro3;

const StyledSubIntro3Wrapper = styled.div`
  width: 100%;
  height: 100%;
`;

const StyledSubIntro3ContentWrapper = styled.div`
  width: 80%;
  height: 100%;
  margin: 0 auto;
`;

const StyledMapImage = styled.img``;
const StyledFilterImage = styled.img`
  width: 56px;
  aspect-ratio: 1/1;
`;