package dto;

import java.time.LocalDateTime;

import model.user.UserGameState;

    public class UserGameDTO {

        public final Long userId;
        public final Long gameId;

        public final boolean estimated;
        public final UserGameState state;
        public final double hours;
        public final LocalDateTime lastPlayed;

        public UserGameDTO(
                Long userId,
                Long gameId,
                boolean estimated,
                UserGameState state,
                double hours,
                LocalDateTime lastPlayed
        ) {
            this.userId = userId;
            this.gameId = gameId;
            this.estimated = estimated;
            this.state = state;
            this.hours = hours;
            this.lastPlayed = lastPlayed;
        }
    }
