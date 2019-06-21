
#RUN npm install -g http-server

## install project dependencies
#RUN npm install

#RUN npm run build
#
#EXPOSE 8080
#CMD [ "http-server", "dist" ]

FROM node:lts-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
#RUN npm run build
EXPOSE 2999
ENTRYPOINT ["npm", "run", "serve"]

# production stage, the second stage is responsible for serving such artifact using NGINX.
#FROM nginx:stable-alpine as production-stage
#COPY --from=build-stage /app/dist /usr/share/nginx/html
#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]