FROM node:10
COPY ./ /app
WORKDIR /app
RUN npm install && npm run build

FROM nginx
RUN mkdir /app
COPY --from=0 /app/dist /app
COPY nginx.conf /etc/nginx/nginx.conf


#FROM node:lts-alpine
#WORKDIR /app
#COPY package*.json ./
#RUN npm instal
#COPY . .
#RUN npm install -g http-server
#RUN npm run build
#CMD [ "http-server", "dist", "-p", "8001"]
#ENTRYPOINT ["npm", "run", "serve"]

# production stage, the second stage is responsible for serving such artifact using NGINX.
#FROM nginx:stable-alpine as production-stage
#COPY --from=build-stage /app/dist /usr/share/nginx/html
#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]